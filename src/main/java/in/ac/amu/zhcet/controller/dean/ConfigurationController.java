package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.configuration.ConfigurationModel;
import in.ac.amu.zhcet.data.model.dto.Config;
import in.ac.amu.zhcet.data.model.dto.mapper.ConfigurationMapper;
import in.ac.amu.zhcet.service.core.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    private Config toConfig(ConfigurationModel configurationModel) {
        Config config = ConfigurationMapper.MAPPER.toConfig(configurationModel);
        config.setTerm(configurationModel.getSession().charAt(0));
        config.setYear(2000 + Integer.parseInt(configurationModel.getSession().substring(1)));

        return config;
    }

    private ConfigurationModel toConfigModel(Config config) {
        ConfigurationModel configurationModel = ConfigurationMapper.MAPPER.fromConfig(config);
        configurationModel.setSession(config.getTerm() + String.valueOf(config.getYear() - 2000));

        return configurationModel;
    }

    @GetMapping("/dean/configuration")
    public String configuration(Model model) {
        model.addAttribute("page_title", "Configuration");
        model.addAttribute("page_subtitle", "Configuration Management Panel");
        model.addAttribute("page_description", "Manage site wide configurations and parameters");

        if (!model.containsAttribute("config"))
            model.addAttribute("config", toConfig(configurationService.getConfig()));
        return "dean/configuration";
    }

    @PostMapping("/dean/configuration")
    public String configurationPost(RedirectAttributes redirectAttributes, @Valid Config config, BindingResult result) {
        String redirectUrl = "redirect:/dean/configuration";

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("config", config);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.config", result);
            return redirectUrl;
        }

        List<String> errors = new ArrayList<>();
        if (config.getThreshold() < 50 || config.getThreshold() > 100)
            errors.add("Threshold should be 50% and 100%");
        if (config.getYear() < 2000)
            errors.add("Year should be greater than 2000");
        if (config.getTerm() != 'A' && config.getTerm() != 'W')
            errors.add("Term can only be Autumn or Winter");

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("config", config);
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectUrl;
        } else {
            configurationService.save(toConfigModel(config));
            redirectAttributes.addFlashAttribute("success", Collections.singletonList("Configuration successfully saved!"));
        }

        return redirectUrl;
    }

}
