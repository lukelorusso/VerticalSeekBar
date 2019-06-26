VerticalSeekBar
===============

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html) [![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21) [![Download](https://api.bintray.com/packages/lukelorusso/maven/com.lukelorusso:verticalseekbar/images/download.svg?version=1.1.0) ](https://bintray.com/lukelorusso/maven/com.lukelorusso:verticalseekbar/1.1.0/link)

## Presentation ##

![Demo](demo.gif)

This is the source code of an Android library: `-=:[ VerticalSeekBar ]:=-`  
Show a cartesian trend graph based on calendar dates

- - -

## Why would you need it? ##

"Reinventing the wheel is, most of the time, a bad idea."  
If you've ever tried to make a SeekBar work properly, you know this is not the case! 😏  
From the moment you apply that `android:rotation="270"` all start to get messy: you cannot set the proper height to the drawable; the width of the bar distorts the width of your drawable; even customize your interaction with the bar is kind of tricky!  
I've been through all of this and suddenly I've had enough.

Introducing a real, nicer, redesigned, vertical and highly customizable SeekBar.

What you got:
- custom progress drawable: apply resources or color gradients
- custom thumb: chose your size, background color, corner radius or even to completely hide it
- view width and drawable width completely separated
- choose how to set your progress (just with the thumb, clicking around, or both)
- min and max placeholders
- much more!

- - -

## How to use it? ##

Make sure to include the library in your app's build.gradle:

```groovy
    implementation 'com.lukelorusso:verticalseekbar:1.1.0'
```  

Add the view to your layout:
```xml
<com.lukelorusso.verticalseekbar.VerticalSeekBar
        android:layout_width="160dp"
        android:layout_height="280dp"/>
```  

maybe add some attributes... here you got some, we'll discuss them later
```
        ...
        app:vsb_click_to_set_progress="false"
        app:vsb_drawable_corner_radius="15dp"
        app:vsb_drawable_width="15dp"
        app:vsb_drawable_background="#f2f2f2"
        app:vsb_drawable_progress_gradient_end="#4dd0e1"
        app:vsb_drawable_progress_gradient_start="#03a2ba"
        app:vsb_max_placeholder_src="@drawable/max_placeholder"
        app:vsb_min_placeholder_src="@drawable/min_placeholder"
        app:vsb_progress="50"
        app:vsb_show_thumb="true"
        app:vsb_thumb_container_corner_radius="5dp"
        app:vsb_thumb_container_tint="@color/placeholderBackground"
        app:vsb_thumb_placeholder_src="@drawable/thumb_placeholder"
        app:vsb_use_thumb_to_set_progress="true"
        ...
```  

All of them can be also set programmatically.

- - -

## Attributes ##

...

More coming soon

- - -

## Copyright ##

The App: Copyright 2019 LUCA LORUSSO - http://lukelorusso.com
