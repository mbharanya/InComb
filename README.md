_Does it feel like you’re wading through honey to get your news? Save time – use_
![InComb](/WebContent/img/logo.png?raw=true)


A dynamic personalized news feed.
InComb used RSS feeds to fetch news, parse them add them to a Lucene index and display them nicely to the user.
The user is able to set his/her news preferences in an intuitive and beautiful interface:

![User Settings](/docs/user-settings.png?raw=true)

To check out a running version of InComb visit http://incomb.com

## Documenation
If you speak German there is a rather complete technical and user documentation in `docs/de`

## How to run InComb
1. Create a new MySQL database from the schema in `src/main/sql/schema.sql`
2. Execute `src/main/sql/data.sql`
3. Adjust your settings in `WebContent/WEB-INF/conf/incomb_config.json`

## Deploying InComb
1. Use scripts in the `deploy` folder to run InComb in production

## How can you contribute?
### Nice to have features / improvements
* Currently the register site is accessable while logged in (using the url)
* Read more in "MyComb" doesn't use the reading view and opens a new tab.
* Change refresh of moment.js, currently it's 1s, could also be 1 min
* Account delete option in frontend
* E-mail tld .codes etc does not work
* Spam protection Comments / Login / Register
* Register Captcha, Email activation link
* add <noscript> tag? Content?
* set max contentLength in parser?
* 404 default servlet error page
* My Comb Read View
* News Open In New Tab
