cmake_minimum_required(VERSION 3.22.1)
project("zygisk")
include_directories(dobby)
add_library(${CMAKE_PROJECT_NAME} SHARED
        main.cpp)
find_library(log-lib log)
target_link_libraries(${CMAKE_PROJECT_NAME}
        ${log-lib}
        ${CMAKE_SOURCE_DIR}/dobby/${CMAKE_ANDROID_ARCH_ABI}/libdobby.a)