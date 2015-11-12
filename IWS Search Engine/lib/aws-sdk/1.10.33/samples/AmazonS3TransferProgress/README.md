# Amazon S3 Transfer Progress Sample

This is a sample that demonstrates how to track transfer progress for uploads to Amazon S3 using the AWS SDK for Java.

## Prerequisites

*   You must have a valid Amazon Web Services developer account.
*   Requires the AWS SDK for Java. For more information on the AWS SDK for Java, see [http://aws.amazon.com/sdkforjava](http://aws.amazon.com/sdkforjava).
*   You must be signed up to use Amazon S3. For more information on Amazon S3, see [http://aws.amazon.com/s3](http://aws.amazon.com/s3).

## Running the Sample

The basic steps for running the Amazon S3 sample are:

1.  Create a credentials file in the location ~/.aws with name "credentials".

2.  Under the `default` profile fill in your Access Key ID and Secret Access Key:

  ```
  [default]
  aws_access_key_id =
  aws_secret_access_key =
  ```

3.  Save the file.

4.  Run the `S3TransferProgressSample.java` file, located in the same directory as the properties file. The sample prints information to the standard output.

**NOTE:** The sample also includes an Ant build.xml file to run the sample.

See the [Amazon S3 Getting Started Guide](http://docs.amazonwebservices.com/AmazonS3/latest/gsg/) for step-by-step instructions on running this sample.
