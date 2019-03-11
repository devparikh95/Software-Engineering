package hello;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Controller
public class controller {

    @Value("${accesskey}")
    String accesskey;
    @Value("${secretkey}")
    String secretkey;
    @Value("${bucketName}")
    String bucketName;
    

   
    @GetMapping("/index")
    public ModelAndView greeting(){
    	
        ModelAndView mv = new ModelAndView("index");
       
        return mv;
    }

    @PostMapping(value = "/FunnyGuy")
    public ModelAndView uploads3(@RequestParam("photo") MultipartFile image, @RequestParam(name = "desc") String desc) {
        ModelAndView returnPage = new ModelAndView();
        System.out.println("description" + desc);
    
        BasicAWSCredentials cred = new BasicAWSCredentials(accesskey, secretkey);
      
        AmazonS3 client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred))
                .withRegion(Regions.US_EAST_1).build();
        try {
            PutObjectRequest put = new PutObjectRequest(bucketName, image.getOriginalFilename(),
                    image.getInputStream(), new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(put);

            String imgSrc = "http://" + bucketName + ".s3.amazonaws.com/" + image.getOriginalFilename();

            returnPage.setViewName("showImage");
            returnPage.addObject("name", desc);
            returnPage.addObject("imgSrc", imgSrc);
        } catch (IOException e) {
            
            e.printStackTrace();
            returnPage.setViewName("error");
        }
        return returnPage;

    }

}