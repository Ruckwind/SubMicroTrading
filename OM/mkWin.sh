#*******************************************************************************
# Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing,  software distributed under the License 
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#*******************************************************************************
#!/bin/ksh

{
cd ..
rm -fr dist
mkdir dist
mkdir dist/class
mkdir dist/jars
mkdir dist/logs
mkdir dist/tmp
mkdir dist/lib

cp -r OM/config dist
cp -r OM/data dist
cp -r OM/var dist
cp -r OM/var dist
cp -r OM/setup dist
cp Core/native/core/bin/linux dist/lib
cp OM/scripts/run/* dist
cp OM/scripts/run/hotspot_compiler dist/.hotspot_compiler
chmod +x dist/*sh

cp -r ./core/native/core dist
rm -fr dist/core/target/*.o
cp core/native/core/bin/win32/* dist/lib


for DIR in Core Generated model OM
do
	cd $DIR/bin

	cp -r * ../../dist/class

	cd ../..
done

cp Core/exlib/jars/* dist/jars
cp OM/exlib/jars/* dist/jars

rm -f dist.tar dist.zip

tar -cvf dist.tar dist
zip dist.zip dist.tar

} 2>&1 | tee mkWin.log



