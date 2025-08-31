package com.api.garagemint.garagemintapi.service.storage;

import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.uploads.root}")
    private String rootDir;

    @Value("${app.uploads.base-url}")
    private String baseUrl;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg","image/png","image/webp","image/gif"
    );

    /** Dosyayı {root}/{subdir} altına yazar ve public URL döner. */
    public String saveImage(MultipartFile file, String subdir) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("file is required");
        }
        String ct = file.getContentType();
        if (ct == null || !ALLOWED.contains(ct.toLowerCase())) {
            throw new ValidationException("unsupported image type");
        }

        String ext = switch (ct) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default -> ".bin";
        };

        try {
            Path dir = Path.of(rootDir, subdir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String name = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = dir.resolve(name);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            return base + "/" + StringUtils.trimLeadingCharacter(subdir, '/') + "/" + name;
        } catch (IOException e) {
            throw new ValidationException("file save failed: " + e.getMessage());
        }
    }
}
