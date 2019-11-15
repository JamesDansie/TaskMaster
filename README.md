# TaskMaster
### Author: James Dansie

This app is for making todo lists on Android. 

### Installation
Clone the repo, then open with android studio.

### Home Page
This is little out of date. Background color has been updated.
![](screenshots/homepage.png)
### Add Task Page
![](screenshots/addtask.png)
### All Tasks Page
![](screenshots/alltasks.png)
### Detail View
![](screenshots/detail.png)
### Settings
![](screenshots/settings.png)

### Changelog
2019/10/22
Built out the home page, add task, and all tasks  

2019/10/23
Built out the detail view, and settings page. Added shared preferences to find the user name.

2019/10/24
Added Recycle view and event listener to the homepage.

2019/10/29
Added a web server.

2019/10/30
Added dynamoDB to store tasks in the cloud.

2019/10/31
Added teams to dynamoDB. Front page filters based off of the team in settings.

2019/11/05
Added Auth log in. Front page displays the user name, and changes on log out.

2019/11/13
A bunch happened in between here. Notifications are set up. Location data is stored in the task. Added the ability to share a picture with the App. Added picture storage in s3 that will show in the detail view.

2019/11/14
Added analytics. Can log sessions, add task, and detail views. 

2019/11/5
Updated read me and made final apk