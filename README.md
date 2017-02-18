_Does it feel like you’re wading through honey to get your news? Save time – use_
![InComb](/WebContent/img/logo.png?raw=true)


A dynamic personalized news feed.
InComb uses RSS feeds to fetch news, parse them, add them to a Lucene index and display them nicely to the user.
The user is able to set his/her news preferences in an intuitive and beautiful interface:
![User Settings](/docs/startpage.png?raw=true)


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
Take a look at the issues page

## FAQ
- Problem: Categories redirect to a 404

  Solution: Recreate categories index (api/indexes/com.incomb.server.categories.indexing.CategoryIndexType)
