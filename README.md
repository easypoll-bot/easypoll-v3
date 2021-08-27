[bot-invite]: https://easypoll.me/invite?utm_source=github&utm_medium=readme&utm_campaign=easypoll
[discord-invite]: https://easypoll.me/discord
[license]: https://github.com/fbrettnich/easypoll-bot/blob/main/LICENSE
[docs]: https://docs.easypoll.me/
[faq]: https://docs.easypoll.me/faq
[troubleshooting]: https://docs.easypoll.me/
[guilds-shield]: https://img.shields.io/badge/dynamic/json?color=7289DA&label=Servers&query=guilds&url=https%3A%2F%2Feasypoll.me%2Fapi.php
[users-shield]: https://img.shields.io/badge/dynamic/json?color=7289DA&label=Users&query=users&url=https%3A%2F%2Feasypoll.me%2Fapi.php
[discord-shield]: https://discord.com/api/guilds/552156123734474762/widget.png
[license-shield]: https://img.shields.io/github/license/fbrettnich/easypoll-bot?label=License
[faq-shield]: https://img.shields.io/badge/Wiki-FAQ-blue.svg
[troubleshooting-shield]: https://img.shields.io/badge/Wiki-Troubleshooting-red.svg
[workflowstatus-shield]: https://img.shields.io/github/workflow/status/fbrettnich/easypoll-bot/Java%20CI%20with%20Maven?event=push&label=Build

[ ![guilds-shield][] ][bot-invite]
[ ![users-shield][] ][bot-invite]
[ ![discord-shield][] ][discord-invite]
[ ![license-shield][] ][license]
[ ![faq-shield][] ][faq]
[ ![troubleshooting-shield][] ][troubleshooting]
[ ![workflowstatus-shield][] ](https://github.com/fbrettnich/easypoll-bot/actions/workflows/java-ci-maven.yml)

<img align="right" src="https://raw.githubusercontent.com/fbrettnich/easypoll-bot/main/.github/images/easypoll-logo.png" height="200" width="200">

# EasyPoll Discord Bot

With EasyPoll, a Discord Poll Bot, you can easily create polls and your members can vote by clicking on a reaction very easily and quickly.

## Invite
If you want to use **EasyPoll** on your server, you can invite it via the following link:  
&#128279; **[https://easypoll.me/invite][bot-invite]**

You can also find help on how to invite the bot in our Docs: [Add Bot to Server](https://docs.easypoll.me/getting-started/add-bot-to-server)

## Usage & Commands

| Command            | Description                                                                                                             |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------- |
| /poll              | Create a normal poll without time limit and with up to 20 own answers                                                   |
| /timepoll          | Create a timed poll, set an end date until when the poll will run. Use time specifications for this like 10m / 3h / 1d  |
| /closepoll         | Close a poll so that no more votes are counted and display the final result                                             |
| /easypoll          | Get a list and help with all commands                                                                                   |
| /vote              | Vote for the EasyPoll Bot                                                                                               |
| /invite            | Invite EasyPoll to your own Discord Server                                                                              |
| /info              | Show some information about EasyPoll                                                                                    |
| /ping              | See the Ping of the Bot to the Discord Gateway                                                                          |
| /setup language    | Change the Bot language of the current Guild                                                                            |
| /setup permissions | Check the required bot permissions on server and channel level                                                          |

## Getting Help
If you have any questions about using the EasyPoll Bot, feel free to visit the official [EasyPoll Support Discord][discord-invite]  
Alternatively you can also have a look at our [Docs][docs] and [FAQ][faq].

## Contributing to EasyPoll
If you want to contribute to EasyPoll, follow these steps:
- [Fork](https://github.com/fbrettnich/easypoll-bot/fork) this repository
- Make your changes
- Check your changes
- Create a [pull request](https://github.com/fbrettnich/easypoll-bot/pulls)
    - **Important:** Create pull requests only in our `development` or `feature` branch, pull requests to the `main` branch will be rejected!

Please also read the [contributing guidelines](https://github.com/fbrettnich/easypoll-bot/blob/main/.github/CONTRIBUTING.md) to contribute properly!

We are happy about any contribution &#9786;

## Dependencies
- [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA/)
- [mongodb-driver](https://github.com/TenorioStephano/MongoDB)
- [mysql-connector-j](https://github.com/mysql/mysql-connector-j)
- [emoji-java](https://github.com/vdurmont/emoji-java)
- [sentry-java](https://github.com/getsentry/sentry-java)
- [unirest-java](https://github.com/Kong/unirest-java)
- [json-simple](https://github.com/fangyidong/json-simple)

## Self-Hosting
Running your own version of EasyPoll Bot is not supported.  
No help is provided for editing, compiling or building this code.  
All changes must follow the [license][license].
