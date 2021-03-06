# Engage MS Teams Clone

## Project Overview: 
Developed an Android application cloning Microsoft Teams for Engage Program 2021 using **agile development methodology.** Using this application people can connect via video calls and also chat in groups or individually.

**Note: The master branch contains the code for the android application and main branch contains the code of the implementaion of video calling**

## Table of Contents
1. [Major Features of Android Application](#major-Features-of-the-android-application)
2. [Features of the Video Call](#features-of-the-video-call)
3. [Technologies used](#technologies-used)
4. [Installation Instructions](#installation-instructions)

#### APK of my application: https://drive.google.com/file/d/1GvKs_SvHukpDgkyi76w3JH8sIkWKv6lO/view?usp=sharing
## Major Features of the Android Application

#### Authentication
- Email id and password login and signup
- Facility to reset password

#### Video calling
- Joining a video call meet with code from the app
- Generate a new meeting link and share with people
- Joining with link possible from both web browser and phone browser

#### Scheduling a meet
- Saving the team details and meet time
- Sending email invites to people

#### Upcoming meets and Invites
- Displays all your invites with accept and reject option
- Displays all your upcoming meets with join, share and delete option

#### Teams
- Automatic team formation when a user schedules a meet
- Adding of invited people when they accept the invite 
- Video calling within the team
- Chatting before, during and after the meet and storing it

#### One-to-one Chatting
- Find friends
- Send connection requests
- Chat with your friends
- See their last seen

#### Profile updation 
- Update your username and status

## Features of the Video Call
- Preview before starting the meet to preset the audio and video options
- In meet team chats(get stored in the team formed on the app)
- Toggle Video and Audio
- Send Invite to other people

## Technologies used

#### Video Calling
- Peer JS for creating a P2P data or media stream connection to a remote peer.
- Socket.io for realtime, bi-directional communication between web clients and servers. 

#### Android Application
- Firebase Authentication using email id and password
- Firebase Realtime Database

## Installation Instructions
1. Directly open the application using the apk
###### OR
1. Open the master branch in your android studio.
2. Clone the main branch for web application.
3. Open account in Google Firebase.
4. Create a new project in Firebase.
5. Add both web and android apps to it.
6. Add the google-services.json file in your app
7. Add the firebase code in script.js file of main branch.
8. Deploy the main branch on any hosting site (Eg : Heroku in my case)  
