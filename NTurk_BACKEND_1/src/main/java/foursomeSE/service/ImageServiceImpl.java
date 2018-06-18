package foursomeSE.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import foursomeSE.error.InvalidFileException;
import foursomeSE.util.FileUtil;
import javafx.beans.property.IntegerProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// 这个类根本没用的

@Service
public class ImageServiceImpl implements ImageService {


//    @Value("${upload.file.extensions}")
//    private String validExtensions;
//
////    @Autowired
////    private FileDao fileDao;
//
////    private String getFileExtension(String fileName) {
////        int dotIndex = fileName.lastIndexOf(".");
////        if (dotIndex < 0) {
////            return null;
////        }
////        return fileName.substring(dotIndex + 1);
////    }
////
////    private boolean isValidExtension(String fileName)
////            throws InvalidFileException {
////        String fileExtension = getFileExtension(fileName);
////
////        if (fileExtension == null) {
////            throw new InvalidFileException("No File Extension");
////        }
////
////        fileExtension = fileExtension.toLowerCase();
////
////        for (String validExtension : validExtensions.split(",")) {
////            if (fileExtension.equals(validExtension)) {
////                return true;
////            }
////        }
////        return false;
////    }
////
////    private int getOpenParenthesisIndex(String baseFileName) {
////        int openParIndex = baseFileName.lastIndexOf("(");
////        int closeParIndex = baseFileName.lastIndexOf(")");
////        boolean isParenthesis = openParIndex > 0 &&
////                closeParIndex == baseFileName.length() - 1;
////
////        if (
////                isParenthesis &&
////                        baseFileName.
////                                substring(openParIndex + 1, closeParIndex).
////                                matches("[1-9][0-9]*")
////                ) {
////            return openParIndex;
////        } else {
////            return -1;
////        }
////    }
////
////    private String handleFileName(String fileName, String uploadDirectory)
////            throws InvalidFileException {
////
////        String cleanFileName = fileName.replaceAll("[^A-Za-z0-9.()]", "");
////        String extension = getFileExtension(cleanFileName);
////
////        if (!isValidExtension(cleanFileName)) {
////            throw new InvalidFileException("Invalid File Extension");
////        }
////
////
////        String base = cleanFileName.substring(
////                0,
////                cleanFileName.length() - extension.length() - 1
////        );
////
////        int openParIndex = getOpenParenthesisIndex(base);
////
////        if (openParIndex > 0) {
////            base = base.substring(0, openParIndex);
////            cleanFileName = base + "." + extension;
////        }
////
////        if (Files.exists(Paths.get(uploadDirectory, cleanFileName))) {
////            cleanFileName = base + "(1)." + extension;
////        }
////
////        while (Files.exists(Paths.get(uploadDirectory, cleanFileName))) {
////            String nString = cleanFileName.substring(
////                    base.length() + 1,
////                    cleanFileName.length() - extension.length() - 2
////            );
////            int n = Integer.parseInt(nString) + 1;
////            cleanFileName = base + "(" + n + ")." + extension;
////        }
////
////        return cleanFileName;
////    }
//
//    //    public File uploadFile(MultipartFile file, String uploadDirectory)
    public void uploadFile(MultipartFile file, String fileName)
            throws InvalidFileException, IOException {
//        String fileName = handleFileName(file.getOriginalFilename(), uploadDirectory);
        Path path = Paths.get(FileUtil.getUploadImagePath(), fileName);
        Files.copy(file.getInputStream(), path); // 其实就是这一句

//        String extension = getFileExtension(fileName);
//        String fileBaseName = fileName.substring(
//                0,
//                fileName.length() - extension.length() - 1
//        );
//        return new File(uploadDirectory, fileName, extension, fileBaseName);
    }

    public static void main(String[] args) throws IOException {
        int[] newNo = {0};

        Files.walk(Paths.get(FileUtil.getUploadImagePath())).mapToInt(f -> {
            String fileNameString = f.getFileName().toString();
            int i = fileNameString.lastIndexOf(".");
            if (i == -1) {
                return -1;
            }
            System.out.println(fileNameString);
            String withoutExtension = fileNameString.substring(0, i);
            String extension = fileNameString.substring(i);
            try {
                return Integer.parseInt(withoutExtension);
            } catch (NumberFormatException e) {
                return -1;
            }
        }).max().ifPresent(m -> {
            newNo[0] = m + 1;
        });


    }
//
////    public void save(File uploadedFile) {
////        fileDao.save(uploadedFile);
////    }
//
////    public File findLastFile() {
////        return fileDao.findFirstByOrderByIdDesc();
////        return null;
//    }
}
