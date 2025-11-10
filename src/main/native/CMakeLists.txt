cmake_minimum_required(VERSION 3.10)
project(totemcounterv2_native)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find JNI
find_package(JNI REQUIRED)

# Include directories
include_directories(${JNI_INCLUDE_DIRS})

# Source files
set(SOURCES
    gui_helper.cpp
)

# Create shared library
add_library(totemcounterv2_native SHARED ${SOURCES})

# Link JNI
target_link_libraries(totemcounterv2_native ${JNI_LIBRARIES})

# Platform-specific settings
if(WIN32)
    set_target_properties(totemcounterv2_native PROPERTIES
        PREFIX ""
        SUFFIX ".dll"
    )
elseif(APPLE)
    set_target_properties(totemcounterv2_native PROPERTIES
        PREFIX "lib"
        SUFFIX ".dylib"
    )
else()
    set_target_properties(totemcounterv2_native PROPERTIES
        PREFIX "lib"
        SUFFIX ".so"
    )
endif()








