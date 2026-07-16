$env:GITHUB_TOKEN = Read-Host -Prompt "Enter your GitHub Personal Access Token"
$env:PYTHONIOENCODING = "utf-8"
python bulk_create_dsa_issues.py

