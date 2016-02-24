Hello World
===
This application pulls and displays the various Hello World locations across the United States, providing some actions such as navigation and calling, along with detailed location information.

Usage
---
Before this app can be used, the developer must receive a Google Maps API key and change it in the `app/src/debug/res/values/google_maps_api.xml` file.

Locations View
---
The main Activity shows both a map of all five hello world locations as well as a list of them underneath. If permission for locations has been granted, the list will be sorted by distance from the user. Otherwise, it is sorted alphabetically.

<img src="http://i.imgur.com/zQIDO8l.png" width="400" height="640"/>

Detail View
---
Whenever an office item is clicked from the ListView on the main screen, a DialogFragment appears that has an image, detailed information, and actions to navigate to or call the location.

<img src="http://i.imgur.com/BM74qZB.png" width="400" height="640"/>