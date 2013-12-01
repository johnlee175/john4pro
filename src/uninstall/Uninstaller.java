package uninstall;

import java.io.File;

public class Uninstaller
{
	public static final String SOFTWARE_NAME="johnSoft";
	
	public static void main(String[] args)
	{
		JohnRegistryUtil.cancelAutoStart(SOFTWARE_NAME);
		String path=JohnPathUtil.getSelfJarLaunchAbsolutePath();
		File file=new File(path);
		JohnInstallUtil.deleteDeskLnk(SOFTWARE_NAME+".lnk");
		JohnInstallUtil.deleteMenuLnk(SOFTWARE_NAME);
		JohnInstallUtil.deleteProgramFiles(file.getParent());
	}
}
