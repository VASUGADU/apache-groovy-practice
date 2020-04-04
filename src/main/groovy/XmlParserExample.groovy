import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

def metadataPath = new File('../../../src/main/resources/metadata.xml')
def metadata = new XmlParser().parseText(metadataPath.text)
//println metadata

def builder = new StreamingMarkupBuilder()
builder.encoding = 'UTF-8'
def keyComponentsXml = builder.bind {
    mkp.declareNamespace('http://www.baesystems.com/netreveal/sna/keyComponents')
    keyComponents {
        metadata.KeyComponentTypes.KeyComponentType.each { kc ->
            keyComponent {
                if(kc.Detail.Description.text().trim() != '')
                    mkp.comment "${kc.Detail.Description.text()}"
                name kc.Name.text()
                dataType kc.Detail.KeyComponentDataType.text()
                coldLists {
                    kc.ColdlistEntries.ColdlistEntry.each { coldListEntry ->
                        coldList{
                            term coldListEntry.Term.text()
                            matchType coldListEntry.MatchType.text()
                        }
                    }
                }
            }
        }
    }
}

new File('../../../src/main/resources/keyComponents.xml').withWriter { w ->
    XmlUtil.serialize(keyComponentsXml, w)
}

def path = new File('../../../src/main/resources/keyComponents.xml')
def keyComponents = new XmlParser().parseText(path.text)
println keyComponents.keyComponent[0].name.text() == 'ACCOUNTNO'
assert !keyComponents.keyComponent[0].name.isEmpty()
//println keyComponents instanceof groovy.util.Node // It specifies the return response as the root node

metadata.remove(metadata.KeyComponentTypes).toString().trim()
new File('../../../src/main/resources/test.xml').withWriter { w ->
    XmlUtil.serialize(metadata, w)
}

BufferedReader br = new BufferedReader(new FileReader(new File('../../../src/main/resources/test.xml')))
FileWriter foStream = new FileWriter(new File('../../../src/main/resources/test1.xml'))
BufferedWriter out = new BufferedWriter(foStream)
String strLine
while ((strLine=br.readLine())!=null) {
    if (strLine.length()>0){
        println strLine
        out.write(strLine +'\n')
        out.flush()
    }

}

