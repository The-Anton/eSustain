# eSustain
===========================================

This repository contains the solution code for the apps for the

A “solution app” is an app that contains the code for a completed codelab.

Introduction
------------

###To make India a pollution-free and carbon-neutral country.

######Our application focuses on solving the problem of climate change by utilizing the real-world data that is currently available. Application searches data like air-quality, forest density, etc and uses all the data to calculate a normalized score for each user and recommend them tasks based on their normalized score. Users can also maintain, track, share, and collaborate with other users

###### The application fetches the address of the user through reverse geocoding API from the location coordinates obtained from the app. Using the location we fetch the following data - 

[Air quality data of user location through whether it API](https://www.weatherbit.io/api/airquality-current)
[Forest cover and trees cover data of user's district from Data.gov.in using API](https://data.gov.in/resources/stateut-wise-tree-cover-estimates-india-state-forest-report-isfr-during-2015from-ministry/api#/Resource/get_resource_4b573150_4b0e_4a38_9f4b_ae643de88f09)
[Users district groundwater level data from Data.gov.in using API](https://data.gov.in/resources/district-wise-dynamic-ground-water-resources-july-2017-0)
[Users district source of electricity and consumption data from Data.gov.in. This data is used to calculate the normalized score](https://data.gov.in/resources/capita-consumption-electricity-kwh-2006-07-2011-12)


Pre-requisites
--------------

The samples, along with the course, assume basic competence in software
design and development, as well as some background in computer science.

Specifically, to get started you need:

- Familiarity with the general software development process for object-oriented
applications using an IDE (Integrated Development Environment).
- At least 1-3 years of experience with object-oriented programming and
the Java programming language, or a comparable modern language. 
- Familiarity with the Kotlin programming language.

Android Studio Version
----------------------

During development of this course, all samples were built using Android Studio 3.3,
and the codelabs assume you are using Android Studio 3.3 with corresponding libraries
and available features. See the 
[Android Studio download page](https://developer.android.com/studio) for details.  

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
