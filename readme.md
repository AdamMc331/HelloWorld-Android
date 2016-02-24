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