Hello World
===
This application pulls and displays the various Hello World locations across the United States, providing some actions such as navigation and calling, along with detailed location information.

Usage
---
Before this app can be used, the developer must receive a Google Maps API key and put it in a string resource, or else run the .apk file found in `app/build/outputs/apk/` folder.

Locations View
---
The main Activity shows both a map of all five hello world locations as well as a list of them underneath. If permission for locations has been granted, the list will be sorted by distance from the user. Otherwise, it is sorted alphabetically.

<img src="http://i.imgur.com/zQIDO8l.png" width="400" height="640"/>

Detail View
---
Whenever an office item is clicked from the ListView on the main screen, a DialogFragment appears that has an image, detailed information, and actions to navigate to or call the location.

<img src="http://i.imgur.com/BM74qZB.png" width="400" height="640"/>

A gif of the call button in action can be seen here:

<img src="http://i.imgur.com/PCWPySe.gif" width="400" height="640"/>

Enhancements
---
Ways this could be further include if I had more time are:
- A SyncAdapter that updated the database at a regularly scheduled interval.
- Testing of the ContentProvider, and other unit tests.
- A faster way to load the office image on the detail fragment, instead of using an AsyncTask.