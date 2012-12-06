Intelligent Lock Pattern Project
================================

This project is part of a bigger project named Intelligent Lock Pattern that has the goal of build a behavioral biometric system on Android based on the lock patterns authentication system present on the OS.

The project started as a university course conclusion work in 2012 and now it's open for new ideas and improvements.

The basic idea is make an artificial intelligence that learns how the user draws his pattern on the screen. Using the knowledge learned, the system verifies if the user that is drawing the pattern is authentic through the inserted characteristics during the drawing.

Intelligent Lock Pattern Application
------------------------------------

This is a Eclipse project of an Android application. The goal is to collect the data necessary for the training of the various networks.

This application uses the [GreenDAO](http://greendao-orm.com/) as ORM.

It was developed using the [Android 2.3.3 SDK](http://developer.android.com/) with API 10.

Some of the functionalities present on the application is collection of the users' characteristics and an activity where you can test the network trained with the samples of one user, using the verification through behavioral biometrics.

The others sub projects:
*	[ILP Models Generator](https://github.com/lucasandre/ilp-models-generator)
*	[ILP Network Models](https://github.com/lucasandre/ilp-network-models)
*	[ILP Network Training](https://github.com/lucasandre/ilp-network-training)