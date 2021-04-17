# eSustain🌳

### 👨‍💻 [APK-v1.0.2](https://drive.google.com/file/d/16aOKdinwGVYDdVDWSOJ3VqVG-u7pPWrq/view?usp=sharing)

### Note:-
#### ⚠️Currently only available for Indian Users 🤕

###### 🧪 If you are testing this application from outside India you will be provided with the option to use an Indian location for simulating the application. 
###### ⚠️ If it still crashes, then consider to use the application in an android emulator, using an Indian location



[![E-Sustain](https://user-images.githubusercontent.com/51144829/114654369-a98f3a00-9d07-11eb-9906-8377b7446fb7.png)](https://www.youtube.com/watch?v=m3yw8S2BLtc)




<img src="https://user-images.githubusercontent.com/51144829/112753561-8a00cd80-8ff5-11eb-8b76-e31cf8ee572b.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753164-18744f80-8ff4-11eb-8b2a-6fbb11257b0f.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753383-0810a480-8ff5-11eb-8493-09fe962c1b64.png" width=20% ><img src="https://user-images.githubusercontent.com/51144829/112753932-663e8700-8ff7-11eb-8086-343234290bfd.png" width=20% >  

<img src="https://user-images.githubusercontent.com/51144829/112753879-1f509180-8ff7-11eb-8d37-dbed18307afc.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753880-22e41880-8ff7-11eb-8de7-041f29c1c52f.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753552-85d4b000-8ff5-11eb-802e-1461b4e70f33.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112754118-56737280-8ff8-11eb-84b8-2ef8f8f04b0c.png" width=20% > 

<img src="https://user-images.githubusercontent.com/51144829/112753547-840aec80-8ff5-11eb-8f4b-b49744aed8ba.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753726-76a23200-8ff6-11eb-8d99-7b99e7138e3a.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753873-16f85680-8ff7-11eb-9715-3821f24d8612.png" width=20% >  <img src="https://user-images.githubusercontent.com/51144829/112753539-7eada200-8ff5-11eb-96ac-e3b1cf0706da.png" width=20% > 

<img src="https://user-images.githubusercontent.com/51144829/112753730-7c981300-8ff6-11eb-87f1-6d44fca9c4ba.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753734-815cc700-8ff6-11eb-87c2-b7ca62af45cf.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112753877-1b247400-8ff7-11eb-9312-f5f6b9c8e5a2.png" width=20% > <img src="https://user-images.githubusercontent.com/51144829/112754178-9175a600-8ff8-11eb-81a2-d05934e1318a.png" width=20% >

Version - 1

### [Backend Server](https://github.com/The-Anton/eSustain_Backend)


Introduction
------------

Our application focuses on solving the problem of climate change by utilizing the real-world data that is currently available. The Application searches data like air-quality, forest density and ground water level in the user area and uses all the data to calculate a normalized score for each user and recommend them tasks based on their normalized score. Users can also maintain, track, share, and collaborate with other users.

The application fetches the address of the user through reverse geocoding API from the location coordinates obtained from the app. Using the location we fetch the following data - 

1. [Air quality data of user location through whether it API](https://www.weatherbit.io/api/airquality-current)

2. [Forest cover and trees cover data of user's district from Data.gov.in using API](https://data.gov.in/resources/stateut-wise-tree-cover-estimates-india-state-forest-report-isfr-during-2015from-ministry/api#/Resource/get_resource_4b573150_4b0e_4a38_9f4b_ae643de88f09)

3. [Users district groundwater level data from Data.gov.in using API](https://data.gov.in/resources/district-wise-dynamic-ground-water-resources-july-2017-0)



Pre-requisites
--------------

To modify or edit the source code we suggest some pre-requisites

1. Android Studio
2. Android Device or Emulator installed along with Android Studio
3. Minimum supported Android SDK

⭐ Your can always install the app with the latest release apk if you are unable to build the application localy 👨‍💻 [APK-v1.0.2](https://drive.google.com/file/d/16aOKdinwGVYDdVDWSOJ3VqVG-u7pPWrq/view?usp=sharing)

Android Studio Version
----------------------

During development of this application, it built using Android Studio 4.0
[Android Studio download page](https://developer.android.com/studio) for details.  

Support Version
----------------------

Minimun SDK Support Version - 21

Target SDK Support Version - 30

## Getting the sample code

Get the latest sample code from GitHub using Git or download the repository as a ZIP file.
([Download](https://github.com/The-Anton/eSustain/archive/refs/heads/main.zip))

    git clone https://github.com/The-Anton/eSustain.git

Getting Started
---------------

1. [Install Android Studio](https://developer.android.com/studio/install.html),
2. [Download the project](https://github.com/The-Anton/eSustain/archive/refs/heads/main.zip)
3. Open the project into Android Studio.
4. Build the project and run the sample. You may need to update gradle and library versions. 
Follow the guidance provided by Android Studio. 
5. If you build the project and finds an error regarding Cuberto/liquid-swipe-android, then consider this [Cuberto/liquid-swipe-android](https://github.com/Cuberto/liquid-swipe-android) github repo to add your credentials into the project
6. If you still not able to build the project try installing the [APK](https://drive.google.com/file/d/16aOKdinwGVYDdVDWSOJ3VqVG-u7pPWrq/view?usp=sharing) of the applicaiton
