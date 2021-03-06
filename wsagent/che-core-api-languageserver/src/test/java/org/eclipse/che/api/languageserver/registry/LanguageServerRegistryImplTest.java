/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.languageserver.registry;

import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.ServerCapabilities;
import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.TextDocumentService;
import io.typefox.lsapi.services.WindowService;

import org.eclipse.che.api.languageserver.launcher.LanguageServerLauncher;
import org.eclipse.che.api.languageserver.shared.model.LanguageDescription;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Anatoliy Bazko
 */
@Listeners(MockitoTestNGListener.class)
public class LanguageServerRegistryImplTest {

    private static final String PREFIX       = "file://";
    private static final String FILE_PATH    = "/projects/1/test.txt";
    private static final String PROJECT_PATH = "/1";

    @Mock
    private ServerInitializer                   initializer;
    @Mock
    private LanguageServerLauncher              languageServerLauncher;
    @Mock
    private LanguageDescription                 languageDescription;
    @Mock
    private LanguageServer                      languageServer;
    @Mock
    private InitializeResult                    initializeResult;
    @Mock
    private ServerCapabilities                  serverCapabilities;
    @Mock
    private CompletableFuture<InitializeResult> completableFuture;

    private LanguageServerRegistryImpl registry;

    @BeforeMethod
    public void setUp() throws Exception {
        when(completableFuture.get()).thenReturn(initializeResult);
        when(initializeResult.getCapabilities()).thenReturn(serverCapabilities);

        when(languageServerLauncher.getLanguageDescription()).thenReturn(languageDescription);
        when(languageServerLauncher.isAbleToLaunch()).thenReturn(true);
        when(languageDescription.getLanguageId()).thenReturn("id");
        when(languageDescription.getFileExtensions()).thenReturn(Collections.singletonList("txt"));
        when(languageDescription.getMimeTypes()).thenReturn(Collections.singletonList("plain/text"));

        when(languageServer.getTextDocumentService()).thenReturn(mock(TextDocumentService.class));
        when(languageServer.getWindowService()).thenReturn(mock(WindowService.class));
        when(languageServer.initialize(any(InitializeParams.class))).thenReturn(completableFuture);

        registry = spy(new LanguageServerRegistryImpl(Collections.singleton(languageServerLauncher),
                                                      null,
                                                      initializer));

        when(initializer.initialize(any(LanguageServerLauncher.class), anyString())).thenAnswer(invocation -> {
            Object[] arguments = invocation.getArguments();
            registry.onServerInitialized(languageServer, serverCapabilities, languageDescription, (String)arguments[1]);
            return languageServer;
        });

        doReturn(PROJECT_PATH).when(registry).extractProjectPath(FILE_PATH);
    }

    @Test
    public void testFindServer() throws Exception {
        LanguageServer server = registry.findServer(PREFIX + FILE_PATH);

        assertNotNull(server);
        assertEquals(server, languageServer);
        verify(initializer).initialize(eq(languageServerLauncher), eq(PROJECT_PATH));
        verify(registry).onServerInitialized(eq(languageServer), eq(serverCapabilities), eq(languageDescription), eq(PROJECT_PATH));
    }
}
