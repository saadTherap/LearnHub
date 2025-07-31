package net.therap.app.controller;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.helper.ContentHelper;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import net.therap.app.service.ContentService;
import net.therap.app.validation.OnCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@RestController
@RequestMapping("/api/contents")
public class ContentController {
    
    private final Logger logger = LoggerFactory.getLogger(ContentController.class);
    
    private final ContentService contentService;
    
    private final DtoHelper dtoHelper;
    
    private final ContentHelper contentHelper;
    
    @Autowired
    public ContentController(ContentService contentService, DtoHelper dtoHelper, ContentHelper contentHelper) {
        this.contentService = contentService;
        this.dtoHelper = dtoHelper;
        this.contentHelper = contentHelper;
    }
    
    @GetMapping("/byModule/{moduleId}")
    public List<ContentCatalogueDTO> getContentByModuleId(@PathVariable long moduleId) {
        List<Content> contents = contentService.findByModuleId(moduleId);
        
        return contents.stream().map(dtoHelper::toContentCatalogueDTO).toList();
    }
    
    @GetMapping("/detail/{contentReleaseId}")
    public ResponseEntity<ContentCatalogueDTO> getContentReleaseById(@PathVariable long contentReleaseId) {
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        return contentOptional.map(
                content -> new ResponseEntity<>(dtoHelper.toDetailedContentCatalogueDTO(content.getCurrentContentRelease()), HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<ContentCatalogueDTO>> getAllContentReleases() {
        List<ContentRelease> contentReleases = contentService.findAllContents();
        
        return new ResponseEntity<>(contentReleases.stream()
                                            .map(dtoHelper::toDetailedContentCatalogueDTO).toList(),HttpStatus.OK);
    }
    
    @GetMapping("/{contentReleaseId}/releases")
    public ResponseEntity<List<ContentRelease>> getAllContentReleases(@PathVariable long contentReleaseId) {
        List<ContentRelease> contentReleases = contentService.findAllReleases(contentReleaseId);
        
        return new ResponseEntity<>(contentReleases, HttpStatus.OK);
    }
    
    @GetMapping("/{contentReleaseId}/releases/{releaseNum}")
    public ResponseEntity<ContentRelease> getSpecificContentRelease(@PathVariable long contentReleaseId, @PathVariable long releaseNum) {
        return ResponseEntity.ok(contentService.findSpecificContentRelease(contentReleaseId, releaseNum));
    }
    
    @PostMapping
    public ResponseEntity<ContentCatalogueDTO> createContent(@RequestBody @Validated(OnCreate.class) ContentCatalogueDTO contentCatalogueDTO) {
        Content content = new Content();
        content.setTitle(contentCatalogueDTO.getTitle());

        ContentRelease contentRelease = contentHelper.getContentRelease(contentCatalogueDTO);
        
        return new ResponseEntity<>(dtoHelper.toDetailedContentCatalogueDTO(contentRelease), HttpStatus.CREATED);
    }
}