import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:cloud_firestore_odm/cloud_firestore_odm.dart';
import 'package:json_annotation/json_annotation.dart';

part 'query.g.dart';

@Collection<DateTimeQuery>('firestore-example-app/42/date-time')
final dateTimeQueryRef = DateTimeQueryCollectionReference();

@JsonSerializable()
@FirestoreDateTimeConverter()
class DateTimeQuery {
  DateTimeQuery(this.time);
  final DateTime time;
}

class FirestoreDateTimeConverter extends JsonConverter<DateTime, Timestamp> {
  const FirestoreDateTimeConverter();
  @override
  DateTime fromJson(Timestamp json) => json.toDate();

  @override
  Timestamp toJson(DateTime object) => Timestamp.fromDate(object);
}

@Collection<TimestampQuery>('firestore-example-app/42/timestamp-time')
final timestampQueryRef = TimestampQueryCollectionReference();

@JsonSerializable()
@FirestoreTimestampConverter()
class TimestampQuery {
  TimestampQuery(this.time);
  final Timestamp time;
}
