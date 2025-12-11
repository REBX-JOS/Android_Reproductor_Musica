// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenLocal()
        maven {
            url = uri("/usr/local/lib/android/sdk/extras/android/m2repository")
        }
        maven {
            url = uri("/usr/local/lib/android/sdk/extras/google/m2repository")
        }
        mavenCentral()
        maven {
            url = uri("https://maven.google.com")
            isAllowInsecureProtocol = false
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
    }
}

allprojects {
    repositories {
        mavenLocal()
        maven {
            url = uri("/usr/local/lib/android/sdk/extras/android/m2repository")
        }
        maven {
            url = uri("/usr/local/lib/android/sdk/extras/google/m2repository")
        }
        mavenCentral()
        maven {
            url = uri("https://maven.google.com")
            isAllowInsecureProtocol = false
        }
    }
}