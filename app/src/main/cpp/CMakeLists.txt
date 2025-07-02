cmake_minimum_required(VERSION 3.22.1)
project(photoeditor)

add_library(photoeditor SHARED
        photoeditor.cpp
)

find_library(log-lib log)
find_library(android-lib android)
find_library(jnigraphics-lib jnigraphics)

target_link_libraries(photoeditor
        ${log-lib}
        ${android-lib}
        ${jnigraphics-lib}
)