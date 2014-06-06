package ca.ubc.cs.commandrecommender.commandinfogen.utils;

import java.util.Collection;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.keys.IBindingService;

public class CommandInfoUtils {
	
	// TODO: design decision
	// We should consider initializing bindingService and
	// command Service in the Activator to reduce redundancy
	// TODO: decide whether to return null or ""
	// TODO: document how to deal with the null value being returned
	//          and remember that null values can be returned.
	// TODO: watch for possible NPE: 
	//          getAdapter() could return null but with ICommandService.class,
	//          it shouldn't.

	public static String getKeyBindingFor(String commandId) {
		IBindingService bindingService = (IBindingService) 
				PlatformUI.getWorkbench().getAdapter(IBindingService.class);
		if (bindingService == null)
			return null;
		return bindingService.getBestActiveBindingFormattedFor(commandId);
	}

	public static String getCommandName(String commandId) {
		ICommandService commandService = getCommandService();
		try {
			return commandService.getCommand(commandId).getName();
		} catch (NotDefinedException e) {
			return commandService.getCommand(commandId).getId();
		}
	}
	
	public static Command getCommand(String commandId) {
		ICommandService commandService = getCommandService();
		return commandService.getCommand(commandId);
	}
	
	public static String getCommandHelpContextId(String commandId) {
		ICommandService commandService = getCommandService();
		try {
			return commandService.getHelpContextId(commandId);
		} catch (NotDefinedException e) {
			return null;
		}
	}

	public static String getCommandDescription(String commandId) {
		ICommandService commandService = getCommandService();
		try {
			return commandService.getCommand(commandId).getDescription();
		} catch (NotDefinedException e) {
			return null;
		}
	}
	
	public static Collection<String> getAllCommands() {
		ICommandService commandService = getCommandService();
		return commandService.getDefinedCommandIds();
	}
	
	public static ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
	}
	
}
