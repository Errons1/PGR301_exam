package com.example.s3rekognition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.TextDetection;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

import java.io.InputStream;
import java.util.List;

@Component
public class TextRekognition {

    @Value("${aws-region:eu-west-1}")
    String region;

    /**
     * Takes in an input stream from a file and sends that file to AWS for text detection
     * Based on Java V2 URL: <a href="https://docs.aws.amazon.com/rekognition/latest/dg/text-detecting-text-procedure.html">AWS Docs</a>
     * @param inputStream from a file
     * @return String result from AWS text detection
     * */
    public String detectTextLabels(InputStream inputStream) throws RekognitionException {
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create("default")).build();

        SdkBytes sourceBytes = SdkBytes.fromInputStream(inputStream);
        Image image = Image.builder().bytes(sourceBytes).build();

        DetectTextRequest textRequest = DetectTextRequest.builder().image(image).build();

        DetectTextResponse textResponse = rekognitionClient.detectText(textRequest);
        List<TextDetection> textCollection = textResponse.textDetections();
        
        StringBuilder builder = new StringBuilder();
        builder.append("Detected lines and words");
        for (TextDetection text : textCollection) {
            builder.append("Detected: ").append(text.detectedText()).append("\n");
            builder.append("Confidence: ").append(text.confidence().toString()).append("\n");
            builder.append("Id : ").append(text.id()).append("\n");
            builder.append("Parent Id: ").append(text.parentId()).append("\n");
            builder.append("Type: ").append(text.type()).append("\n");
            builder.append("\n");
        }
        
        rekognitionClient.close();
        return builder.toString();
    }
}
