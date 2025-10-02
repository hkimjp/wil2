# upload

:db/id   <id>
:wil2    "upload"
:login   <login>
:md      <markdown>
:date    <uploaded day>
:updated <jt/local-date-time>}


# point

:db/id   <id>
:wil2    "point"
:login   <login>
:to/id   <eid>
:pt      <int>
:updated <jt/local-date-time>}


# redis

wil2:<user>     list of rating times
wil2:<user>:eid list of eids to which have sent ratings
wil2:<user>:pt  last answered time
