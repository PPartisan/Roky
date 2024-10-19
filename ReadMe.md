
# Roky

## Project Description
"Roky" is a desktop command-line application that allows small groups of authorised users to securely communicate. Version 1 will start with simple features, but the app is designed to easily grow into a GUI desktop or mobile application as we develop it further. Planned features include group collaboration tools, such as polling systems, calendar scheduling, and APIs for data gathering and research.

## Features
- Users can authenticate and sign in.
- Authenticated users can join a single chat room to communicate.
- Chat history is retained remotely for access at each login.
- Users can customize limited aspects of their profile.
- Includes Help and About sections with project and contribution information.

## Installation
During development:
1. Clone the repository using git CLI or "Import from VCS" in IntelliJ. See our Wiki for more information on using git and the IDE.
2. If using IntelliJ, start the app with the "Run" button in a virtual terminal.
3. From the command line, build the project using `./gradlew assemble`, navigate to `<project-root>/build/libs`, and run the `.jar` file with `java -jar <jar-file-name>`.

No prerequisites are required if using the IntelliJ IDE.

## Usage
Navigate with the "Tab" or arrow keys. Press "Escape" to return to the main menu from any screen.
- Start by selecting "Login" and entering your username and password.
- After authentication, the "Join Chat Room" option appears.
- In the chat room, type messages in the input box and press "Enter" to send them.

## Contributing
We’re excited to welcome contributors of all skill levels! We’ve created templates to help everyone make meaningful contributions, whether you're just getting started or are a seasoned coder. We value pair and group programming so that everyone can ask questions during development, ensuring the best possible understanding and code quality. Post a message on an open issue you’re interested in, or contact a collaborator to join in on group coding sessions.

## License
This project is licensed under the [GNU GPLv3 License](https://www.gnu.org/licenses/gpl-3.0.en.html).

## Roadmap
Looking ahead, we have exciting plans for Chat App beyond the MVP:
- Add polling features for data gathering.
- Develop APIs to share collected data with researchers.
- Create graphical frontends for mobile and desktop.
- Introduce calendar features for task scheduling.
- Support multiple chat rooms with room management tools.

## Contact
Interested in contributing? Comment on an open issue, and a collaborator will invite you to join the Discord, WhatsApp, and Calendar groups to schedule work together. Plans for a GitHub discussion board are in progress.
