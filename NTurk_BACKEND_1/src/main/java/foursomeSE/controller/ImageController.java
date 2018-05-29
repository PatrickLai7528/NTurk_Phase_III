package foursomeSE.controller;


import foursomeSE.entity.communicate.UploadImageResponse;
import foursomeSE.error.InvalidFileException;
import foursomeSE.service.ImageServiceImpl;
import foursomeSE.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;


@RestController
public class ImageController {
//    @RequestMapping(value = "/image/{filename}",
//            method = RequestMethod.GET,
//            produces = MediaType.IMAGE_JPEG_VALUE)
////    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename) throws IOException {
//        ClassPathResource imgFile = new ClassPathResource("data/images/" + filename);
//        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
//
//
//
//        return ResponseEntity
//                .ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(bytes);
//    }

//    @RequestMapping(value = "/image/{filename}",
//            method = RequestMethod.POST)
//    public ResponseEntity<?> uploadImage() {
//        return null;
//    }

//    @Value("${upload.file.directory}")
//    private String uploadDirectory;

    private ImageServiceImpl fileService;

    public ImageController(ImageServiceImpl fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/image/{filename}", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(Model model, @RequestParam("file") MultipartFile file, @PathVariable("filename") String filename) {

//            File uploadedFile = fileService.uploadFile(file, uploadDirectory);
//            fileService.save(uploadedFile);

        try {
            fileService.uploadFile(file, filename);
            return new ResponseEntity<>(new UploadImageResponse(null), HttpStatus.OK);
        } catch (InvalidFileException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            fileService.uploadFile(file, file.getOriginalFilename());
            return new ResponseEntity<>(new UploadImageResponse(null), HttpStatus.OK);
        } catch (InvalidFileException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/image/{filename}"
            , method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getImage(@PathVariable("filename") String filename) throws IOException {
//        Path filePath = fileService.findLastFile().getFilePath();
//        System.out.println(LocalDate.now() + "filename" + filename);

        Path filePath;
        if (filename.equals("hamburger1.png")) {
            ClassPathResource imgFile = new ClassPathResource("data/images/" + filename);
            byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);

        } else {
            filePath = Paths.get(FileUtil.getUploadImagePath() + filename);
            return ResponseEntity
                    .ok()
                    .contentLength(Files.size(filePath))
                    .contentType(
                            MediaType.parseMediaType(
                                    URLConnection.guessContentTypeFromName(filePath.toString())
                            )
                    )
                    .body(new InputStreamResource(
                            Files.newInputStream(filePath, StandardOpenOption.READ))
                    );
        }
    }
}
