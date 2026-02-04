# IllegalStack-OG

A fork of IllegalStack maintained by [TrueOG Network](https://true-og.net). IllegalStack is a spigot based plugin dedicated to fixing glitches and exploits that have made it into final Minecraft releases.

## Changes from IllegalStack:

- Completely removed references to proprietary JetsMinions API.

- Disabled config updates.

- Builds with TrueOG Network's config file, including detailed in-line documentation.

- Updated gradle from 8.1.1 to 8.14.3.

## Building this project

Gradle is the recommended way to build the project. Use `./gradlew clean build` in the main project directory to build the 
project.
The output is located at `/build/libs/IllegalStack<version>.jar`.
