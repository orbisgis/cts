# CTS 
[![GitHub](https://img.shields.io/github/license/orbisgis/cts.svg)](https://github.com/orbisgis/cts/blob/master/docs/LICENSE.md) 
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d9b34e00264d4702b8340f8544cec21f)](https://www.codacy.com/gh/orbisgis/cts?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=orbisgis/cts&amp;utm_campaign=Badge_Grade)

Coordinate Transformation Suite (abridged CTS)  is a library developed to
perform coordinate transformations using well known geodetic algorithms and
parameter sets.  It strives to be simple, flexible and interoperable, in this
order.

#### History

Originally developed by Michaël Michaud under the name [JTransfoCoord](http://michael.michaud.free.fr/index_geodesie.html) and then JGeod, the library was
renamed to CTS in 2009.  Today CTS is maintained by the OrbisGIS team in
collaboration with Michaël Michaud.

The new CTS has been funded by the French *Agence Nationale de la Recherche* (ANR) under
research contract ANR-08-VILL-0005-01 and the regional council of the *Région Pays
de La Loire* under the *Système d'Orbservation Géographique de la Ville*
(SOGVILLE) project.


#### License

CTS is free software; you can redistribute it and/or modify it under the terms
of the GNU Lesser General Public License as published by the Free Software Foundation,
either version 3 of the License.

CTS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with
CTS.  If not, see <http://www.gnu.org/licenses/>.

#### Declaring project dependencies

You can include CTS in your project thanks to Maven repositories.

From maven central, check https://search.maven.org/artifact/org.orbisgis/cts/1.5.2/bundle

To use the current snapshot add in the pom

```xml
<repository>
  <id>orbisgis-snapshot</id>
  <name>OrbisGIS sonatype snapshot repository</name>
  <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```

and the following dependency

```xml
<dependency>
    <groupId>org.orbisgis</groupId>
    <artifactId>cts</artifactId>
    <version>1.6.0-SNAPSHOT</version>
</dependency>
```

