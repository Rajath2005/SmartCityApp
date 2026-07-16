#!/usr/bin/env python3
"""
Alternative: Minimal issue creator using GitHub REST API + requests library
Use if PyGithub is unavailable or you prefer direct API calls.
"""

import os
import time
import json
import sys

try:
    import requests
except ImportError:
    print("ERROR: requests library not installed. Install with: pip install requests")
    sys.exit(1)


class DSAIssueCreatorREST:
    """Create DSA issues using GitHub REST API + requests"""

    def __init__(self, repo_owner: str, repo_name: str):
        self.token = os.getenv("GITHUB_TOKEN")
        if not self.token:
            raise ValueError(
                "GITHUB_TOKEN environment variable not set. "
                "Export it: export GITHUB_TOKEN=<your-PAT>"
            )
        
        self.repo_owner = repo_owner
        self.repo_name = repo_name
        self.base_url = f"https://api.github.com/repos/{repo_owner}/{repo_name}"
        self.headers = {
            "Authorization": f"token {self.token}",
            "Accept": "application/vnd.github.v3+json"
        }
        self.created_issues = []
        self.failed_issues = []

    def get_or_create_labels(self, label_specs: list[dict]) -> dict:
        """Get existing labels or create new ones"""
        labels = {}
        
        # Fetch existing labels
        try:
            resp = requests.get(f"{self.base_url}/labels", headers=self.headers)
            resp.raise_for_status()
            existing = {l["name"]: l for l in resp.json()}
        except Exception as e:
            print(f"Failed to fetch labels: {e}")
            existing = {}
        
        for spec in label_specs:
            name = spec["name"]
            if name in existing:
                labels[name] = existing[name]
                print(f"  ✓ Label '{name}' already exists")
            else:
                try:
                    resp = requests.post(
                        f"{self.base_url}/labels",
                        headers=self.headers,
                        json={
                            "name": name,
                            "color": spec.get("color", "0366d6"),
                            "description": spec.get("description", "")
                        }
                    )
                    resp.raise_for_status()
                    labels[name] = resp.json()
                    print(f"  ✓ Created label '{name}'")
                except Exception as e:
                    print(f"  ✗ Failed to create label '{name}': {e}")
        
        return labels

    def create_issue(self, title: str, body: str, labels: list[str]) -> dict:
        """Create a single issue via REST API"""
        try:
            resp = requests.post(
                f"{self.base_url}/issues",
                headers=self.headers,
                json={
                    "title": title,
                    "body": body,
                    "labels": labels
                }
            )
            resp.raise_for_status()
            issue_data = resp.json()
            
            result = {
                "success": True,
                "issue_number": issue_data["number"],
                "url": issue_data["html_url"],
                "error": None
            }
            self.created_issues.append(result)
            print(f"  ✓ Created issue #{issue_data['number']}: {title}")
            return result
        
        except Exception as e:
            result = {
                "success": False,
                "issue_number": None,
                "url": None,
                "error": str(e)
            }
            self.failed_issues.append(result)
            print(f"  ✗ Failed to create '{title}': {e}")
            return result

    def create_wave(self, wave_name: str, issues: list[dict]) -> dict:
        """Create all issues in a wave"""
        print(f"\n{'='*70}")
        print(f"Creating {wave_name}")
        print(f"{'='*70}\n")
        
        results = {
            "wave": wave_name,
            "total": len(issues),
            "created": 0,
            "failed": 0,
            "issues": []
        }
        
        for i, issue_spec in enumerate(issues, 1):
            print(f"[{i}/{len(issues)}] Creating: {issue_spec['title']}")
            result = self.create_issue(
                title=issue_spec["title"],
                body=issue_spec["body"],
                labels=issue_spec.get("labels", [])
            )
            results["issues"].append(result)
            if result["success"]:
                results["created"] += 1
            else:
                results["failed"] += 1
            
            # Rate limiting
            if i < len(issues):
                time.sleep(1)
        
        return results

    def print_summary(self, all_results: list[dict]):
        """Print execution summary"""
        print(f"\n{'='*70}")
        print("SUMMARY")
        print(f"{'='*70}\n")
        
        for result in all_results:
            print(f"Wave: {result['wave']}")
            print(f"  Created: {result['created']}/{result['total']}")
            if result['failed'] > 0:
                print(f"  Failed: {result['failed']}/{result['total']}")
        
        if self.created_issues:
            print(f"\nCreated Issues:")
            for issue in self.created_issues:
                print(f"  #{issue['issue_number']}: {issue['url']}")


if __name__ == "__main__":
    print("To use this alternative REST API version, call DSAIssueCreatorREST from your script.")
    print("\nExample:")
    print("  creator = DSAIssueCreatorREST('Rajath2005', 'SmartCityApp')")
    print("  labels = creator.get_or_create_labels([...])")
    print("  result = creator.create_wave('Wave 1', [...])")
