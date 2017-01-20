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
package org.eclipse.che.ide.command.toolbar;

import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.ide.api.command.ContextualCommand;
import org.eclipse.che.ide.command.toolbar.button.PopupItem;

/**
 * Contains {@link ContextualCommand} and {@link Machine}
 */
public class MachinePopupItem implements PopupItem {

    private final ContextualCommand command;
    private final Machine machine;
    private final String name;

    public MachinePopupItem(ContextualCommand command, Machine machine) {
        this.command = command;
        this.machine = machine;
        this.name = machine.getConfig().getName();
    }

    public MachinePopupItem(MachinePopupItem item) {
        this.command = item.command;
        this.machine = item.machine;
        this.name = command.getName() + " in " + machine.getConfig().getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    public ContextualCommand getCommand() {
        return command;
    }

    public Machine getMachine() {
        return machine;
    }
}