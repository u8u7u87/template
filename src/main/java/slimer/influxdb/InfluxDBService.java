package slimer.influxdb;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class InfluxDBService {
	public void test() {
		System.out.println("begin influxdb test");
		InfluxDB influxDB=InfluxDBFactory.connect("http://localhost:8086", "root", "root");
		String dbNameString="test";
		BatchPoints batchPoints=BatchPoints.database(dbNameString).retentionPolicy("default")
                .consistency(ConsistencyLevel.ALL)
                .build();
		Point point1 = Point.measurement("t").field("hot", 90).field("reg", 9).field("te", 1).field("value", 5).build();
		batchPoints.point(point1);
		influxDB.write(batchPoints);
		QueryResult queryResult=influxDB.query(new Query("select * from t", dbNameString));
		System.out.println(queryResult.toString());
		System.out.println("end influxdb test");
	}
	
	public boolean save(String dataBaseName, List<String> data) {
		
		return true;
	}
}
