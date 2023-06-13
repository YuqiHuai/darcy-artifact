import os
import xml.etree.ElementTree as etree

f_pkg_graph = open("pkg_graph.txt", "w")
f_class_pkg_graph = open("class_pkg_graph.txt", "w")
f_interface_abstract_classes = open("interface_abstract_classes.txt", "w")

pkgList = set()

for root, dirs, files in os.walk("XMLreports/"):
    for file in files:
        if file.endswith(".xml"):
            e = etree.parse(os.path.join(root, file)).getroot()
            for pkgs in e.findall("packages"):
                for pkg in pkgs.findall("package"):
                    for ref in pkg.findall("packageRef"):
                        f_pkg_graph.write(
                            pkg.get("name") + ":" + ref.get("name") + "\n"
                        )
                pkgList.add(pkg.get("name"))

            for classes in e.findall("classes"):
                for cl in classes.findall("class"):
                    if cl.get("type") in ["interface", "abstract class"]:
                        f_interface_abstract_classes.write(
                            cl.get("name") + ":" + cl.get("type") + "\n"
                        )
                        for ref in cl.findall("classRef"):
                            if ref.get("type") == "usedBy":
                                f_interface_abstract_classes.write(
                                    cl.get("name")
                                    + "-Used By-"
                                    + ref.get("name" + "\n")
                                )
                    for pkg in pkgList:
                        if cl.get("name").startswith(pkg):
                            f_class_pkg_graph.write(pkg + ":" + cl.get("name") + "\n")
                            break

f_pkg_graph.close()
f_class_pkg_graph.close()
f_interface_abstract_classes.close()

print(":::: Number of Packages = ", len(pkgList), " ::::")
# print(":::: Length of `pkgList`` = ",len(pkgList), " ::::")
# print(":::: Number of Unique Packages = ",len(set(pkgList)), " ::::")
