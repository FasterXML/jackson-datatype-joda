Project: jackson-datatype-joda

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.13.0 (not yet released)

No changes since 2.12

2.12.1 (08-Jan-2021)

#120: Cache formatter with offset parsed (performance improvement)
 (contributed by Guido M)

2.12.0 (29-Nov-2020)

#116: Improve schema support for DateTimeZone type
 (contributed by Joost v-d-W)
#117: Timestamp deserialization not working for CSV, Properties or XML
#119: Baseline Joda dependency now 2.10.8 (but should work with 2.9.x still)
 (requested by Pierre G)

2.11.4 (12-Dec-2020)
2.11.3 (02-Oct-2020)
2.11.2 (02-Aug-2020)
2.11.1 (25-Jun-2020)

No changes since 2.11.0

2.11.0 (26-Apr-2020)

#104: Deserializing Interval discards timezone information
 (contributed by Richard W)
- Now requires use of Joda 2.9.x (wrt #104 fix)

2.10.5 (21-Jul-2020)

No changes since 2.10.4

2.10.4 (03-May-2020)

#113: `ObjectMapper.setDefaultLeniency()` is causing `NullPointerException`
  in `JacksonJodaDateFormat`
 (reported by Bertrand R)

2.10.3 (03-Mar-2020)
2.10.2 (05-Jan-2020)

No changes since 2.10.1

2.10.1 (09-Nov-2019)

#108: `JodaDateSerializer` Discards Shape Override Preference
 (reported by Adrian R; fix contributed by Vincent B)

2.10.0 (26-Sep-2019)

- Add JDK9 `module-info.class` using Moditect plugin
- Update Joda dependency to 2.9.9 (should still work with older versions too)

2.9.10 (21-Sep-2019)
2.9.9 (16-May-2019)
2.9.8 (15-Dec-2018)

No changes since 2.9.7

2.9.7 (19-Sep-2018)

#99: Binary compatibility broken in 2.9.x in `DateTimeSerializer`
 (reported, fixed by andrewl102@github)
#101: Instant, YearMonth and MonthDay not use pattern in @JsonFormat
 (reported, fixed by Adrian P)

2.9.6 (12-Jun-2018)
2.9.5 (26-Mar-2018)
2.9.4 (24-Jan-2018)
2.9.3 (09-Dec-2017)
2.9.2 (14-Oct-2017)
2.9.1 (07-Sep-2017)

No changes since 2.9.0

2.9.0 (30-Jul-2017)

#90: `DurationDeserializer` does not accept valid ISO8601 duration values
 (requested by bpelakh@github)
#93: ADJUST_DATES_TO_CONTEXT_TIME_ZONE got wrong result when parse string contains zone id
 (contributed by Daniel Q)

2.8.9 (12-Jun-2017)
2.8.8 (05-Apr-2017)
2.8.7 (21-Feb-2017)
2.8.6 (12-Jan-2017)
2.8.5 (14-Nov-2016)
2.8.4 (14-Oct-2016)
2.8.3 (17-Sep-2016)
2.8.2 (30-Aug-2016)
2.8.1 (20-Jul-2016)

No changes since 2.8.0.

2.8.0 (04-Jul-2016)

#83: WRITE_DATES_WITH_ZONE_ID feature not working when applied on @JsonFormat annotation
 (reported by mandyWW@github)
#87: Add support for JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
 (contributed by Alexey B)
- Support "config overrides" for `@JsonFormat.Value` added in databind
- Upgrade joda-time dependency to 2.7

2.7.8 (26-Sep-2016)
2.7.7 (27-Aug-2016)
2.7.6 (23-Jul-2016)
2.7.5 (11-Jun-2016)
2.7.4 (29-Apr-2016)
2.7.3 (16-Mar-2016)

No changes since 2.7.2

2.7.2 (27-Feb-2016)

#81: Add key deserializers for `Duration` and `Period` classes
 (contributed by Jochen S)
- Change build to produce JDK6-compatible jar, to allow use on JDK6 runtime

2.7.1 (02-Feb-2016)
2.7.0 (10-Jan-2016)

No changes since 2.6.

2.6.6 (05-Apr-2016)

#82: Can't deserialize a serialized DateTimeZone with default typing
 (reported by Chris P)

2.6.5 (19-Jan-2016)
2.6.4 (07-Dec-2015)
2.6.3 (12-Oct-2015)

No changes since 2.6.2.

2.6.2 (15-Sep-2015)

#71: Adjust LocalDate / LocalDateTime deserializer to support
  `DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE`
 (contributed by Luke N)
#73: `SerializationFeature.WRITE_DATES_WITH_ZONE_ID` writes
  inconsistent Zone Offset
 (contributed by Fabian B)

2.6.1 (09-Aug-2015)

- Removed `final` from serializer implementations, to allow sub-class overriding

2.6.0-1 (29-Jul-2015) (micro-patch)

#70: Default DateTime parser format is stricter than previous versions,
  causing incompatibility
 (reported by jamesmcmillan@github)

2.6.0 (19-Jul-2015)

#49: testDateMidnightSerWithTypeInfo test dependent on $TZ
 (contributed by Michal Z)
#58: Support timezone configuration for Interval deserializer
#62: Allow use of numbers-as-Strings for LocalDate (in array)
 (contributed by Michal Z)
#64: Support `@JsonFormat(pattern=...)` for deserialization
#66: Support `SerializationFeature.WRITE_DATES_WITH_ZONE_ID` 
#68: TimeZone in DeserializationContext is ignored with
   `SerializationFeature.WRITE_DATES_WITH_ZONE_ID`
 (contributed by Jerry Y, islanderman@github)

2.5.4 (09-Jun-2015)

#60: Configured date/time format not considered when serializing Joda Instant
 (reported by Thorsten P)

2.5.3 (24-Apr-2015)
2.5.2 (29-Mar-2015)

No changes since 2.5.1

2.5.1 (06-Feb-2015)

#51: Calling `JodaDateSerializerBase.isEmpty()` results in a `StackOverflowError`.
 (reported, fix contributed by Charlie L-M)

2.5.0 (01-Jan-2015)

2.4.6 (23-Apr-2015)
2.4.5 (14-Jan-2015)
2.4.4 (24-Nov-2014)

No changes since 2.4.3

2.4.3 (04-Oct-2014)

#45: Can't use LocalTime, LocalDate & LocalDateTime as Key type for a Map
 (contributed by Brad K, reported by Guido M)
#46: Interval deserialization fails for negative start instants
 (reported by Dan G, dgrabows@github)

2.4.2 (15-Aug-2014)

No changes since 2.4.0

2.4.1 (17-Jun-2014)

No changes since 2.4.0

2.4.0 (03-Jun-2014)

#40: Add support for `ReadablePeriod`
 (contributed by 'wimdeblauwe@github')
- Joda dependency now to 2.2

2.3.3 (14-Apr-2014)

#32: Support use of `@JsonFormat(shape=...)` for timestamp/string choice

2.3.2 (01-Mar-2014)

#16: Adjust existing Date/Time deserializers to support `DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE`
 (contributed by lukelukeluke@github)

2.3.1 (28-Dec-2013)

#19: Add serializers and deserializers for MonthDay and YearMonth
 (contributed by ncjones@github)
#21: Make `DateTimeSerializer` use configured timezone
 (contributed by pavax@github)
#23: Package as a bundle (was accidentally not, just bare jar)
 (suggested by Łukasz D)
#24: Allow serializing `Duration` using ISO-8601 notation
 (requested by cowwoc@github)
#25: Add `KeyDeserializer` for `DateTime`
 (contributed by Lorcan C)
#26: Implement `equals()` and `hashCode()` for JodaModule
 (suggested by Henning S)
#27: Add support for `DateTimeZone`
 (requested by Hendy Irawan)

2.3.0 (14-Nov-2013)

#18: Add `JodaMapper`, sub-class of basic `ObjectMapper` that auto-registers
  Joda module

2.2.3 (25-Aug-2013)
2.2.2 (27-May-2013)
2.2.1 (04-May-2013)

No functional changes.

2.2.0 (23-Apr-2013)

#8: Make DateTimeDeserializer abide by configured TimeZone
- Upgraded version detection not to use VERSION.txt file

2.1.2 (08-Dec-2012)
2.1.1 (13-Nov-2012)

No functional changes.

2.1.0 (08-Oct-2012)

New minor version, based on Jackson core 2.1.

Improvements:

- [Issue#9]: Add support for (de)serialiazing Interval
 (submitted by jkolobok@github)

2.0.4 (26-Jun-2012)

Improvements:

- [Issue-6] Add support for handling Local date types
 (submitted by Chris Stivers)
- Add support for Joda Instant data type

2.0.3 (15-Jun-2012)

Fixes:

- [Issue-3] Add support for Duration serialization, deserialization
 (reported by Marshall Pierce)

2.0.2 (18-May-2012)

No fixes, just syncing up with core releases.

2.0.1 (29-Mar-2012)

Fixes:

* Issue-1: Deserializers registered so they would handle Object type,
  messing up 'untyped' Lists, Maps
 (reported by Pierre-Alexander M)

2.0.0 (25-Mar-2012)

The first official release
