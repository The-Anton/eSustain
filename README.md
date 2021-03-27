# eSustain🌳
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
if you don't already have it. (If you are just starting the course, you will be guided
through this process.)
2. Download the starter sample for the codelab.
2. Open the sample into Android Studio.
3. Build and run the sample. You may need to update gradle and library versions. 
Follow the guidance provided by Android Studio. 
