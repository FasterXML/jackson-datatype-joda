Here are people who have contributed to the development of Jackson JSON processor
Joda datatype module.
(version numbers in brackets indicate release in which the problem was fixed)

Tatu Saloranta, tatu.saloranta@iki.fi: author

ncjones@github.com:
 * Contributed #19:  Add support for `MonthDay` and `YearMonth`
  (2.3.1)

Łukasz D:
 * Suggested #23: package as a bundle
  (2.3.1)

Lorcan C
 * Contributed #25: Add `KeyDeserializer` for `DateTime`
  (2.3.1)

Hendy Irawan (ceefour@github)
 * Contributed #27: Allow support for `DateTimeZone`
  (2.3.1)

Brad Kennedy (bkenned4@github)
 * Contributed #45: Can't use LocalTime, LocalDate & LocalDateTime as Key type for a Map
  (2.4.3)

Charlie La Mothe (clamothe@github)
 * Contributed #51: Calling `JodaDateSerializerBase.isEmpty()` results in a `StackOverflowError`.
  (2.5.1)

Thorsten Platz (ThorstenPlatz@github)
 * Reported #60: Configured date/time format not considered when serializing Joda Instant
  (2.5.4)

Michał Ziober (ZioberMichal@github)
 * Contributed #62: Allow use of numbers-as-Strings for LocalDate (in array)
  (2.6.0)

Jerry Yang (islanerman@github)
  * Contributed #68: TimeZone in DeserializationContext is ignored with
   `SerializationFeature.WRITE_DATES_WITH_ZONE_ID`
  (2.6.0)

jamesmcmillan@github
  * Reported #70: Default DateTime parser format is stricter than
   previous versions, causing incompatibility

Luke Nezda (nezda@github)
  * Contributed #71: Adjust LocalDate / LocalDateTime deserializer to support
  `DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE`
  (2.6.2)

Fabian Buch (fabianbuch@github)
 * Contributed #73: `SerializationFeature.WRITE_DATES_WITH_ZONE_ID` writes
   inconsistent Zone Offset
  (2.6.3)

Chris Plummer (strmer15@github)
 * Reported #82: Can't deserialize a serialized DateTimeZone with default typing
  (2.6.6 / 2.7.3)

Jochen Schalanda (joschi@github)
 * Contributed #81: Add key deserializers for `Duration` and `Period` classes
  (2.7.2)

Alexey Bychkov (joxerTMD@github)
 * Contributed #87: Add support for JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
  (2.8.0)

Daniel Qian (chanjarster@github)
 * Reported, contributed fix for #93: ADJUST_DATES_TO_CONTEXT_TIME_ZONE got wrong result
   when parse string contains zone id
  (2.9.0)

Adrian Palanques (devdevx@github)
 * Reported, contributed fix for #101: Instant, YearMonth and MonthDay not use
   pattern in @JsonFormat
  (2.9.7)

Richard Wise (Woodz@github)
  * Contributed fix for #104: Deserializing Interval discards timezone information
  (2.11.0)
