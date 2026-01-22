pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 使用 JitPack 仓库
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Bandverse"
include(":app")
