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
```kotlin
// CommandLeafs
CommandEmptyLeaf()
CommandBoolLeaf()
CommandIntLeaf()
CommandDoubleLeaf()
CommandStringLeaf()
CommandPlayerLeaf()
CommandLocationLeaf()
CommandVarargLeaf()
CommandPairLeaf()
CommandTripleLeaf()

// CommandBranch
//   /command examples [first/second/third]
//  this command is to group a lot of command nodes together under 1 key word ("example" in this case)
CommandBranch("example", arrayOf( ..., ... ) )

// Usage
// note that each leaf also has a emptyEffect argument (which is not shown here)
// Only the Empty leaf is shown doing that, that's because that's the only other argument that this leaf has
val exampleCommand = CommandBranch("example", arrayOf(
    CommandEmptyLeaf("emptyArg", { player -> /* empty command logic */ }),
    CommandBoolLeaf("boolArg", { player, boolValue -> /* bool command logic */ }),
    CommandLocationLeaf("locationArg", { player, location -> /* location command logic */ }),
    
    CommandIntLeaf("intArg1", 1/null, 10/null, { player, intValue -> /* int command logic */ }),
    CommandIntLeaf("intArg2", intArrayOf(1,3,5,7), { player, intValue -> /* int command logic */ }),
    CommandIntLeaf("intArg3", 0,  { Teams.count } , { player, intValue -> /* int command logic */ }), 
    // you can also provide real time inputs, it will be checked what the value is whenever you enter the command
    
    CommandDoubleLeaf("doubleArg1", 0.0/null, 1.0/null, { player, doubleValue -> /* double command logic */ }),
    CommandDoubleLeaf("doubleArg2", 0.0, { Distance.max } , { player, doubleValue -> /* double command logic */ }),
    // doubles don't have any possibility for a list

    CommandStringLeaf("stringArg1", null, { player, stringValue -> /* string command logic */ }),                          // anything
    CommandStringLeaf("stringArg2", arrayOf("option1", "option2"), { player, stringValue -> /* string command logic */ }), // only these options
    CommandStringLeaf("stringArg2", { Teams.teamNames }, { player, stringValue -> /* string command logic */ }),           // real time
    
    CommandPlayerLeaf("playerArg1", arrayOf(), { player, targetPlayer -> /* player command logic */ }),                    // fixed
    CommandPlayerLeaf("playerArg2", { Plugin.onlinePlayers }, { player, targetPlayer -> /* player command logic */ }),     // dynamic
    
    // the following 3 leafs need other leafs to operate, the title of the innerLeafs will never be shown, so they can be basicly any name
    // the vararg is a list of infinite arguments of the type you provided, the effect will return a list
    // the boolean is if you allow for it to return empty or not (true if it can, false if not)
    CommandVarargLeaf("varargArg",
        CommandIntLeaf("innerIntArg", 1, 100, { _, _ -> }), false, { player, varargValues -> /* vararg command logic */ }),
    // the pair is 2 argument types, grouped under 1 name (here "pairArg")
    CommandPairLeaf("pairArg", 
        CommandIntLeaf("innerIntArg", 1, 100, { _, _ -> }),
        CommandBoolLeaf("innerBoolArg", { _, _ -> })),
    // the triple is 3 argument types, grouped under 1 name (here "tripleArg")
    CommandTripleLeaf("tripleArg",
        CommandIntLeaf("innerIntArg", 1, 100, { _, _ -> }), 
        CommandBoolLeaf("innerBoolArg", { _, _ -> }), 
        CommandStringLeaf("innerStringArg", arrayOf("option1", "option2"), { _, _ -> }))
))
```

## License

IDK how licenses work. If I ever find the time to assign a license, it will be a license that basically says "do whatever you want with it, Just reference me somewhere in the comments for example"
