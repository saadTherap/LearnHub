package net.therap.app.controller;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@RestController
@RequestMapping("/api/contents")
public class ContentController {
    
    @Autowired
    private ContentService contentService;
    @Autowired
    private DtoHelper dtoHelper;
    
    @GetMapping("/byModule/{moduleId}")
    public List<ContentCatalogueDTO> getContentByModuleId(@PathVariable long moduleId) {
        List<Content> contents = contentService.findByModuleId(moduleId);
        
        return contents.stream().map(dtoHelper::toContentCatalogueDTO).toList();
    }
    
    @GetMapping("/detail/{contentReleaseId}")
    public ContentCatalogueDTO getContentReleaseById(@PathVariable long contentReleaseId) {
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        return contentOptional.map(content -> dtoHelper.toDetailedContentCatalogueDTO(content.getCurrentContentRelease()))
                .orElse(null);
    }
}