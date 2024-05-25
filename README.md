# CommandTree Library

CommandTree is a Kotlin-based library for managing command trees in your applications.

## Installation

You can include CommandTree in your project by adding the JitPack repository to your build configuration and then adding the library as a dependency.

### Gradle

Add the JitPack repository to your `build.gradle.kts` file:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

Add the CommandTree dependency:

```kotlin
dependencies {
    implementation("com.github.WantToBeeMe:CommandTree:1.1.0")
}
```

## Usage

To use CommandTree in your Kotlin application, you need to initialize the `CommandTreeSystem` in your `onEnable` method:

```kotlin
class YourPlugin : JavaPlugin() {

    override fun onEnable() {
        // Initialize CommandTreeSystem with your plugin instance and title
        CommandTreeSystem.initialize(this, "YourPluginTitle")
        
        // Your other initialization code here
    }

    // Your plugin logic here
}
```

You need to provide this initialize method with your plugin instance and a title.
The instance is used internally whenever it needs something related to the plugin (like a list of all players, or subscribing a command)
The title is used for example when an error happens, or any other feedback to the commander.

If you see `commander` in the source code, you will know that is referencing a player which sends the command in the chat.

## Examples

There are 2 examples in the `examples` folder:
- [HelloWorldCommand](examples/HelloWorldCommand.kt): This demonstrates how to use CommandTree with a very basic command
- [GroupCommand](examples/GroupCommand.kt): This provides a more advanced usage scenario.  

and this is the list of partials that can be used
-  BranchPartial
- EmptyPartial 
- BooleanPartial 
- StringPartial 
- PlayerPartial
- IntPartial
- DoublePartial
- LocationPartial
- VarargPartial
- PairPartial
- TriplePartial

## License
IDK how licenses work. If I ever find the time to assign a license, it will be a license that basically says "do whatever you want with it, Just reference me somewhere in the comments for example"
