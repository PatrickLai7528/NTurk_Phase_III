package foursomeSE.controller;


import foursomeSE.entity.communicate.UploadImageResponse;
import foursomeSE.error.InvalidFileException;
import foursomeSE.service.ImageServiceImpl;
import foursomeSE.util.FileUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;


@RestController
public class ResourcesController {
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

    public ResourcesController(ImageServiceImpl fileService) {
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

    @RequestMapping(value = "/json/{taskId}",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('REQUESTER')")
    // 不能折腾这个了，就反回string，然后让前端处理下载吧。
    // 要不就是去掉authorization，然后应该browser访问这个url就会下载，但是这样显然不好
    public InputStreamResource getJson(HttpServletResponse response, @PathVariable("taskId") long taskId) throws IOException {
        // TODO
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"demo.json\"");
        InputStreamResource resource = new InputStreamResource(new FileInputStream("/Users/tiberius/Documents/College/Sophomore2/SE3/BigWork/i3/NTurk_Phase_III/NTurk_BACKEND_1/src/main/resources/data/jsons/message.json"));
        return resource;

//        Path filePath = Paths.get("/Users/tiberius/Desktop/TaskRec- Probabilistic Matrix Factorization.pdf");
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
//
////        Path filePath = Paths.get("/Users/tiberius/Documents/College/Sophomore2/SE3/BigWork/i3/NTurk_Phase_III/NTurk_BACKEND_1/src/main/resources/data/jsons/message.json");
////        return new FileSystemResource(filePath.toFile());
//
//        return ResponseEntity
//                .ok()
//                .contentLength(filePath.toFile().length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(new InputStreamResource(Files.newInputStream(filePath, StandardOpenOption.READ)));

    }

}
