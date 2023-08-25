# Code Zenify

![Build](https://github.com/stephan-james/de-sjd-zenify/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/22498-code-zenify.svg)](https://plugins.jetbrains.com/plugin/22498-code-zenify)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22498-code-zenify.svg)](https://plugins.jetbrains.com/plugin/22498-code-zenify)

Credits go to Dmitry Kandalov (https://github.com/dkandalov/live-plugin) for delivering a good starter point.

<!-- Plugin description -->

# Code Zenify Plugin

This is an IntelliJ IDEA plugin which reduces Java code by all the superfluous rubbish that only disturbs a code review.

There are planty of options to customize your zenification experience:

You can choose to:
- fold blocks
- fold comments
- fold field type elements
- fold getters & setters
- fold import lists
- fold local variable type elements
- fold log statements
- fold methods
- fold modifier lists
- fold parameter type elements
- fold parentheses
- fold semicolons
- fold throw statements
- fold new statements
- fold type parameter lists
- fold arbitrary expressions

Since the zenify mode toggles it is a good idea to provide the zenify action with a specific shortcut.

<!-- Plugin description end -->

### Example

| Original source  | Zenified source           |
|------------------|---------------------------|
| ![Before](assets/LongCode.png)    | ![After](assets/ShortCode.png) |

### Settings
![Settings](assets/Settings.png)


### Installation

To install the plugin, go to Settings > Plugins > Marketplace and search for "Code Zenify" or use the following URL: https://plugins.jetbrains.com/plugin/22498-code-zenify.
