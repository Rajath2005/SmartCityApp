-- =====================================================================
-- populate_dummy_data.sql
-- ---------------------------------------------------------------------
-- Seeds the `places` table with 50 diverse sample places so contributors
-- can test the app without entering data by hand.
--
-- Prerequisite: run db_setup.sql first to create the schema.
--
-- Usage:
--   mysql -u root -p smart_city_guide < populate_dummy_data.sql
--
-- Safe to re-run: INSERT IGNORE skips rows whose id already exists, so
-- this script will not fail or duplicate data on a second run.
-- =====================================================================

USE smart_city_guide;

INSERT IGNORE INTO places (id, name, category, location, description, latitude, longitude) VALUES
-- Parks & Nature
(1,  'Central Riverside Park',   'Park',        'Downtown',      'Sprawling riverside park with walking trails and open lawns.',        12.9716, 77.5946),
(2,  'Sunrise Botanical Garden', 'Park',        'North District','Curated botanical garden with a glasshouse and rose walk.',           12.9850, 77.6100),
(3,  'Lakeview Gardens',         'Park',        'East End',      'Landscaped gardens wrapped around a calm boating lake.',              12.9600, 77.6400),
(4,  'Hillcrest Nature Reserve', 'Park',        'West Hills',    'Protected woodland reserve popular with birdwatchers.',              12.9400, 77.5500),
(5,  'Old Oak Community Green',  'Park',        'Southside',     'Neighbourhood green with a playground and community orchard.',       12.9300, 77.5900),

-- Museums & Culture
(6,  'City History Museum',      'Museum',      'Downtown',      'Permanent galleries tracing four centuries of city history.',        12.9720, 77.5950),
(7,  'Museum of Modern Art',     'Museum',      'Arts Quarter',  'Rotating exhibitions of contemporary painting and sculpture.',       12.9760, 77.5990),
(8,  'Science Discovery Centre', 'Museum',      'North District','Hands-on science exhibits and a planetarium dome.',                  12.9880, 77.6050),
(9,  'Maritime Heritage Museum', 'Museum',      'Harbourside',   'Restored vessels and exhibits on the city trading past.',            12.9500, 77.6300),
(10, 'Railway Transport Museum', 'Museum',      'East End',      'Vintage locomotives and signalling equipment on display.',           12.9620, 77.6450),

-- Restaurants & Cafes
(11, 'The Copper Kettle',        'Restaurant',  'Downtown',      'Family-run bistro serving regional classics all day.',               12.9730, 77.5930),
(12, 'Spice Route Kitchen',      'Restaurant',  'Old Town',      'Slow-cooked curries and clay-oven breads.',                          12.9680, 77.5870),
(13, 'Harbour Grill House',      'Restaurant',  'Harbourside',   'Seafood grill with tables overlooking the marina.',                  12.9510, 77.6320),
(14, 'Green Fork Cafe',          'Cafe',        'Arts Quarter',  'Plant-based cafe with a seasonal menu and roastery.',                12.9770, 77.6010),
(15, 'Morning Bean Coffee',      'Cafe',        'Downtown',      'Small-batch espresso bar and neighbourhood meeting spot.',           12.9740, 77.5920),
(16, 'Sunset Terrace Lounge',    'Restaurant',  'West Hills',    'Hilltop terrace dining with panoramic city views.',                  12.9420, 77.5480),
(17, 'The Night Market Grill',   'Restaurant',  'Old Town',      'Open-air stalls serving street food until late.',                    12.9660, 77.5850),

-- Hospitals & Health
(18, 'City General Hospital',    'Hospital',    'Downtown',      'Full-service public hospital with 24-hour emergency care.',          12.9700, 77.5900),
(19, 'Northside Medical Centre', 'Hospital',    'North District','Multi-speciality centre with outpatient clinics.',                   12.9890, 77.6080),
(20, 'Riverside Childrens Clinic','Hospital',   'East End',      'Paediatric clinic and vaccination centre.',                          12.9610, 77.6410),
(21, 'Westgate Urgent Care',     'Hospital',    'West Hills',    'Walk-in urgent care for minor injuries and illness.',                12.9430, 77.5520),

-- Education
(22, 'Central Public Library',   'Library',     'Downtown',      'Six-floor reference library with free study spaces.',                12.9725, 77.5940),
(23, 'Eastside Branch Library',  'Library',     'East End',      'Community library with a large childrens reading room.',             12.9630, 77.6430),
(24, 'City Technical University','Education',   'North District','Public university focused on engineering and design.',               12.9900, 77.6120),
(25, 'Old Town Grammar School',  'Education',   'Old Town',      'Historic secondary school founded in 1867.',                         12.9670, 77.5860),
(26, 'Harbourside Maritime Academy','Education','Harbourside',   'Vocational academy training marine engineers.',                      12.9505, 77.6310),

-- Shopping
(27, 'Grand Central Mall',       'Shopping',    'Downtown',      'Four-level shopping centre with cinema and food court.',             12.9710, 77.5960),
(28, 'Old Town Bazaar',          'Shopping',    'Old Town',      'Covered market of spice, textile and craft stalls.',                 12.9675, 77.5880),
(29, 'Riverside Farmers Market', 'Shopping',    'East End',      'Weekend market for local produce and baked goods.',                  12.9605, 77.6420),
(30, 'Westfield Retail Park',    'Shopping',    'West Hills',    'Out-of-town retail park with free parking.',                         12.9410, 77.5510),

-- Transport
(31, 'Central Railway Station',  'Transport',   'Downtown',      'Main intercity rail terminus with 12 platforms.',                    12.9780, 77.5730),
(32, 'City International Airport','Transport',  'North District','Two-terminal airport serving domestic and long-haul routes.',        13.1986, 77.7066),
(33, 'Downtown Bus Interchange', 'Transport',   'Downtown',      'Hub for all city and regional bus services.',                        12.9770, 77.5720),
(34, 'Harbour Ferry Terminal',   'Transport',   'Harbourside',   'Passenger ferry terminal with hourly crossings.',                    12.9490, 77.6290),
(35, 'Southside Metro Depot',    'Transport',   'Southside',     'Metro interchange connecting the southern lines.',                   12.9280, 77.5910),

-- Landmarks & Worship
(36, 'Old Clock Tower',          'Landmark',    'Old Town',      'Nineteenth-century clock tower at the heart of the old town.',       12.9665, 77.5875),
(37, 'Victory Memorial Arch',    'Landmark',    'Downtown',      'Stone memorial arch on the main civic boulevard.',                   12.9745, 77.5915),
(38, 'Riverside Suspension Bridge','Landmark',  'East End',      'Pedestrian suspension bridge lit after dark.',                       12.9595, 77.6390),
(39, 'Hilltop Observatory',      'Landmark',    'West Hills',    'Working observatory open to the public on clear evenings.',          12.9390, 77.5460),
(40, 'St Marys Cathedral',       'Worship',     'Old Town',      'Gothic revival cathedral with a noted pipe organ.',                  12.9690, 77.5890),
(41, 'Grand Central Mosque',     'Worship',     'Downtown',      'Congregational mosque with a courtyard garden.',                     12.9735, 77.5905),
(42, 'Lakeside Temple',          'Worship',     'East End',      'Lakeside temple complex with daily evening rituals.',                12.9590, 77.6440),

-- Sports & Entertainment
(43, 'City Sports Stadium',      'Sports',      'Southside',     'Forty-thousand-seat stadium hosting football and athletics.',        12.9250, 77.5950),
(44, 'Aquatic Sports Centre',    'Sports',      'North District','Olympic-size pool, diving boards and public swim sessions.',         12.9870, 77.6140),
(45, 'Northside Tennis Club',    'Sports',      'North District','Twelve courts, coaching, and open club nights.',                     12.9860, 77.6160),
(46, 'Grand Opera House',        'Entertainment','Arts Quarter', 'Restored opera house staging opera, ballet and concerts.',           12.9755, 77.5985),
(47, 'Riverside Amphitheatre',   'Entertainment','East End',     'Open-air amphitheatre for summer concerts.',                         12.9585, 77.6405),
(48, 'Starlight Cinema',         'Entertainment','Downtown',     'Eight-screen cinema with a rooftop deck.',                           12.9715, 77.5965),

-- Civic
(49, 'City Hall',                'Civic',       'Downtown',      'Seat of the city council and public records office.',                12.9750, 77.5935),
(50, 'Southside Community Centre','Civic',      'Southside',     'Community hall hosting classes, clinics and events.',                12.9290, 77.5930);
