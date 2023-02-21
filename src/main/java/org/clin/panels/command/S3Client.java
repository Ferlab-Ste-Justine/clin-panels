package org.clin.panels.command;

import com.typesafe.config.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

public class S3Client {

  private final software.amazon.awssdk.services.s3.S3Client s3Client;

  public S3Client(Config conf) {
    var s3Conf = S3Configuration.builder().pathStyleAccessEnabled(conf.getBoolean("path-style-access")).build();
    var s3Creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(conf.getString("access-key"),conf.getString("secret-key")));
    this.s3Client = software.amazon.awssdk.services.s3.S3Client.builder()
      .credentialsProvider(s3Creds)
      .endpointOverride(URI.create(conf.getString("endpoint")))
      .region(Region.US_EAST_1)
      .serviceConfiguration(s3Conf)
      .httpClientBuilder(ApacheHttpClient.builder()
        .connectionTimeout(Duration.ofMinutes(conf.getInt("timeout")))
        .socketTimeout(Duration.ofMinutes(conf.getInt("timeout")))
      ).build();
  }

  public boolean exists(String bucket, String key) {
    try {
      s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }

  public byte[] getContent(String bucket, String key) throws IOException {
    var request = GetObjectRequest.builder().bucket(bucket).key(key).build();
    return s3Client.getObject(request).readAllBytes();
  }

  public void writeContent(String bucket, String key, String content) {
    var request = PutObjectRequest.builder().bucket(bucket).key(key).build();
    s3Client.putObject(request, RequestBody.fromString(content));
  }
}
