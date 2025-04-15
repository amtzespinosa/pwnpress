![PwnPress](pwnpress-gh.png)

**[PwnPress Framework](http://pwnpress.org/)** is a powerful and automated WordPress vulnerability scanner - the exploitation tool part stills under development and **looking for colaborators**. It can scan WordPress sites (in the same way as **WPScan**) and it aims detect and, in the future, also exploit vulnerabilities in WordPress core, plugins, and themes. **It is completely free, open source and with no API rate limit.**

![version](https://img.shields.io/badge/version-1.2.0-blue)
![build](https://img.shields.io/badge/build-passing-green)
![license](https://img.shields.io/badge/license-GPLv3-lightgrey)
![language](https://img.shields.io/badge/java-17-yellowgreen?stlye=flat&logo=Java)

```
I know, I know, code is a mess. I will sort that in later versions. First I want to craft an amazing tool. 
If anyone out there wants to help, you're welcome!
```

**Contact:** hello@pwnpress.org

## Features

- [x] **Automated scanning**: Automatically scans WordPress websites for info gathering and known vulnerabilities.
- [x] **Sites validation**: It can validate a list of targets to filter only WordPress sites and build a file. It can also filter sites depending on their version status.
- [x] **Directory scraping**: Recursively scrape any directory to list all files in them.
- [x] **Create phishing**: Instantly build a default WordPress login phishing page with credentials collection utility that sends gathered credentials to your inbox.
- [x] **Leverage XML-RPC**: You can leverage XML-RPC for two things: brute force extracted users' passwords and to test for **system.Multicall** so you can pingback other websites (you will need a webserver publicly accesible to test for pingback).
- [x] **Settings Management**: Allows setting constant parameters for scanning and exploitation.
- [x] **Request Crafting**: Constructs and sends HTTP requests with injected payloads.
- [ ] **Response Analysis**: Analyzes server responses to detect vulnerabilities.
- [ ] **Exploitation**: Attempts to exploit detected vulnerabilities.
- [ ] **Multiple Exploitation Techniques**: Supports SQLi, XSS, RCE, file inclusion, path traversal, SSRF, and insecure file uploads.

## Installing & Running 

```
Please, report any bug or problem in the Issues section.
```

### Option 1 - Java:
With Java installed, you can download the *.jar* file and run it with the following command:
`java -jar pwnpress_1.2.0_cli.jar`

### Option 2 - Debian:
Install **openjdk-17-jre**:
`sudo apt install openjdk-17-jre`

Then, download the Debian package and install it with the following comand:
`sudo dpkg -i pwnpress_1.2.0.deb`

Now, run the tool:
`pwnpress`

### Option 3 - Windows:
Download the *.zip* file and extract it. Then, just execute the *.exe* file.


## Basic usage

Once the tool is running, type `help` to show available commands on main menu. From there you can go to other sections and see available commands also typing `help`. Other generic commands: 
- `back`: go back to previous menu. 
- `exit`: quit the tool.
- `settings`: Here you can set/unset some constant values for the tool like number of threads used, constant url, files location...

```
Commands pointed with (*) are still under development and (probably) do not work (at all).
```

|Section|Description|Commands|
|--|--|--|
| **scanner** | Scan the specified WordPress URL/domain. This is the main functionality of the tool. It can extract valuable info from a WordPress website including versions and vulnerabilities. |  |
|  | Perform a regular scan over the specified WordPress URL. | `scan <url>` |
|  | Perform a deep scan over the specified WordPress URL. | `deep-scan <url>` (*) |
| **target** | Validate and extract WP versions and status. This is very useful when you have a big batch of sites in a *.txt* file and want to create a file containing ONLY WordPress sites. |  |
|  | Checks if a single url or domain uses WordPress and saves the urls in a *.txt* file. | `validate <url/domain/file>` |
|  | Checks version status of WordPress for the domains in the file. Flags: `--latest`, `--outdated`, `--insecure`, `--all` or `-a` | `status <domains_file> [flags]` |
| **scraper** | Scrape the specified WordPress directory URL. |  |
|  | Recursively scrapes a given directory and prints all files found. | `scrape` (*) |
| **phisher** | Create a WordPress login phishing page. In order to collect the phished data, the tool is using Static Forms API. In the future I intend to add Mockbin and other similar services. |  |
|  | Generate a phishing page for the default WordPress login page. | `default` |
|  | Generate a phishing page for a custom WordPress login page. | `custom` |
| **bruteforce** | Brute force the specified WordPress page and service. |  |
|  | Bruteforce passwords of a WordPress site abusing XML-RPC **system.Multicall** if enabled. | `xml-rpc` |
|  | Bruteforce common WordPress directories. | `directories` (*) |
| **pingbacker** | Leverage XML-RPC pingback to build a *webnet* and be able to perform DDoS attacks. |  |
|  | Build a file of urls that can perform requests to other sites from a file containing WordPress sites | `webnet <file>` |
|  | Use a webnet file to perform a DDoS attack. | `ddos <url>` (*) |
| **exploit** | Try to exploit found or common WordPress vulnerabilities. This aims to be an automated exploit tool using exploit modules but at the moment is under development. |  |
|  | Search exploitation modules. | `search` (*) |
|  | Load exploitation modules. | `load` (*) |
|  | Run exploitation modules. | `run` (*) |
|  | Send a custom HTTP request to the server. | `request` (*) |
|  | Connect PwnPress Framework with external tools to enhance exploitation. | `external tools` (*) |

## Looking for colaborators

If you have any skills that you consider relevant for the project, let me know! Join the project:

- Send an email to development@guardpress.org
- Or use this form: [http://pwnpress.org/](http://pwnpress.org/#join)
