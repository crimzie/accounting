### crimzie/accounting

This is a demo application showcasing stateful financial transactions handling 
that is built with Play framework (requirement) and Monix library. 

It can be started in dev mode with `sbt run` or in prod mode by producing a
dist with `sbt dist` (that packages to a zip file in `target/universal/`) and
executing generated binaries.

By default it listens on port 9000.

There are three endpoints:
* `POST /api/debit` with `text/plain` body expected to contain transaction 
amount as a positive integer. Response `200` contains balance after transaction 
as `text/plain` integer.
* `POST /api/credit` with `text/plain` body expected to contain transaction
amount as a positive integer. Response `200` contains balance after transaction 
as`text/plain` integer or response `409` contains unchanged balance as 
`text/plain` integer in case of requested transaction exceeding available 
balance.
* `GET /api/history` with query parameters `n` that specifies transactions per 
page and optional `page` that specifies page number starting from 0, both 
expected to be integers. Response `200` contains `application/json` array of 
transactions as positive and negative integers in the order from newest to 
oldest.

All transaction amounts and balance are recorded as integers of whatever 
currency cents to avoid any possible floating point precision inconsistencies, 
so floating point calculations are deferred to frontend if it would be so chosen
to process amounts as floating point numbers. 

The transactions history is stored in a text file (`./balance` by default, can 
be changed in `app/Conf.scala` config) by appending on each transaction and 
available balance is restored from these records on application start.
