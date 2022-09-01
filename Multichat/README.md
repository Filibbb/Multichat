# team01.2-pp-uebung-multichat

## Beschreibung

Dieses Repository enthält den Code für die Übung Multichat. <br>
Zur [Aufgabenstellung](docs/PM2-Uebung-Anleitung.pdf).

## Organisation

Zu Beginn des Projektes haben wir uns mit dem Code vertraut gemacht und gleichzeitig Issues gesucht.
Wir haben alles in den [Issues](https://github.zhaw.ch/PM2-IT21taWIN-bles-gan-kars/Uebung-hk1-fupat002-weberph5/issues?q=is%3Aissue+) 
dokumentiert und mit zwei Issue-Templates gearbeitet. <br>
Das erste grosse Refactoring des `ConnectionHandler` haben wir gemeinsam gemacht, was den unterschiedlichen [Projektbeitrag](https://github.zhaw.ch/PM2-IT21taWIN-bles-gan-kars/Uebung-hk1-fupat002-weberph5/graphs/contributors) auf GitHub erklärt.
Danach haben wir uns auf die einzelnen Issues aufgeteilt und so unsere Lösung erarbeitet. Eine ausführliche Erläuterung unserer Lösung ist [hier](docs/Solution_Explanation.md) ersichtlich. 

## Issues
Alle Issues sind [hier](https://github.zhaw.ch/PM2-IT21taWIN-bles-gan-kars/Uebung-hk1-fupat002-weberph5/issues?q=is%3Aissue+) zu finden und für eine genauere Beschreibung der gelösten Issues siehe: [Documented_Issues.md](docs/Documented_Issues.md) 

## Klassendiagramm

![ClassDiagram_Multichat](https://github.zhaw.ch/storage/user/4879/files/9c8a576e-9030-4774-a2c2-6523bfa27683)

## Team

### Gruppen-Mitglieder
* Philippe Weber
* Patric Fuchs

### Teamrules
* Wir versuchen den Code in Englisch zu schreiben.
* Wir arbeiten grundsätzlich mit Branches. Branches werden auf Englisch benammst und sollten beschreiben, was in diesem Branch gemacht wird.
* Wenn eine Änderung komplett ist, sollte diese im Idealfall in Review bei allen gestellt werden bevor auf den Master Branch gepushed wird. (4 Augen Prinzip)
* Git Commits bitte auf Englisch und nur Zustände comitten, die mindestens kompilieren und nach Möglichkeit nur auf die eigenen Feature Branches.
* Wenn wir feststellen, dass etwas nicht funktioniert bitte frühzeitig melden, wenn die oben genannten Teamrules nur hinderlich sind dies ansprechen, dann werden die Neu definiert.

### Git Workflow

Wir verwenden den Standard Git-Workflow, d.h. wir arbeiten grundsätzlich mit eigenen Branches.

Das Naming Pattern, welches wir auf den Branches verwenden ist Folgendes:
* Für Refactoring Branches: prefix mit `refactoring`
* Für Bugfix Branches: prefix mit `bugfix`

Daraus resultiert z.B. folgender Name für einen Branch: `refactoring/connection-handler` oder `bugfix\fix-button-click-not-working`

Alles Weitere wie z.B. `release` oder vglw. wird für den Rahmen des Projektes nicht benötigt.

Der Hauptbranch ist bei uns der `master`. Auf diesen sollte nicht direkt gepushed werden.

Grundsätzlich ist die Idee mit Pull-Requests zu arbeiten anstelle von direkten Pushes auf den `master`.
Ein Pull-Request wird immer von einem Refactoring/Bug Branch aus gemacht. Pull-Requests sollten einen Zustand repräsentieren, der fertig ist, d.h. nicht noch TODOs oder unaufgeräumten Code enthalten.
Vor dem merge sollte das andere Teammitglied den Pull-Request anschauen und ein Review abgeben. Nach einem Review sollten allfällige Änderungen entweder umgesetzt oder Rückfragen gestellt werden um Unklarheiten oder vglw. aufzulösen. Wenn das Feedback umgesetzt wurde, beginnt der Reviewzyklus von vorne.

Wenn ein Pull-Request gemerged wurde, sollte der dazugehörige Branch immer gelöscht werden. Dies dient dazu, dass keine "toten" branches herumliegen die potentiell verwirren könnten.

Commit Messages sollten nach Möglichkeit widerspiegeln, was gemacht wurde. D.h. nicht "Did stuff" "Fixed" oder vglw. sondern repräsentativ für den Change stehen.

