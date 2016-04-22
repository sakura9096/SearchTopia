import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;

public class S3Operation {
	public static void main (String[] args) {
		String bucketName = args[0];
		String key = args[1];
		String downloadDirectory = args[2];
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (/home/fanglinlu/.aws/credentials), and is in valid format.",
                    e);
        }

        AmazonS3 s3 = new AmazonS3Client(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        s3.setRegion(usEast1);

		download (bucketName, key,credentials, downloadDirectory);
	}
	
	private static void download (String bucketName, String key, AWSCredentials credentials, String downloadDirectory) {
		File file = new File (downloadDirectory);
//		if (!file.exists() || !file.isDirectory()) {
//			System.out.println("The input direcotry does not exists!");
//			return;
//		}
		
		TransferManager transferM = new TransferManager(credentials);
		Download download = transferM.download(bucketName, key, file);
		
		try {
			download.waitForCompletion();
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		transferM.shutdownNow();
		System.out.println("download finished");

		
	}
}
