/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.api.gen.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jubula.toolkit.api.gen.ClassGenerator;
import org.eclipse.jubula.tools.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.utils.generator.ToolkitConfig;
import org.eclipse.jubula.tools.xml.businessmodell.Component;

/**
 * Generates classes for components from comp system
 */
public class APIGenerator {
    
    /** component generator */
    private static ClassGenerator classGenerator =
            new ClassGenerator();
    
    /**
     * Constructor
     */
    private APIGenerator() {
        // hidden
    }

    /** 
     * main
     * @param args args
     */
    public static void main(String[] args) {
        ConfigLoader loader = ConfigLoader.getInstance();
        String generationBaseDir = loader.getGenerationDir();
        ToolkitConfig config = loader.getToolkitConfig();
        CompSystemProcessor processor = new CompSystemProcessor(config);
        
        List<ComponentInfo> compInfos = processor.getCompInfos(false);
        for (ComponentInfo compInfo : compInfos) {
            Component component = compInfo.getComponent();
            createClass(component, generationBaseDir);
        }
    }

    /** 
     * creates class for component
     * @param component the component
     * @param generationBaseDirTemplate directory for generation
     */
    private static void createClass(Component component,
            String generationBaseDirTemplate) {
        GenerationInfo genInfo = new GenerationInfo(component);
        String path = genInfo.getDirectoryPathExtension();
        String className = genInfo.getClassName();
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {genInfo.getToolkitName()});
        File dir = new File(generationBaseDir + path);
        File file = new File(dir, className + ".java"); //$NON-NLS-1$
        String content = classGenerator.generate(genInfo);

        if (!file.exists()) {
            try {
                dir.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            FileOutputStream fop = new FileOutputStream(file);
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}