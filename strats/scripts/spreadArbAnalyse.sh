#*******************************************************************************
# Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing,  software distributed under the License 
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#*******************************************************************************
#!/bin/ksh

grep RECALC A*  | grep -v invalid  | grep " NO " >a3
cat a3 | sed -e "s/^.*spreadSellDiff=//" -e "s/,.*//" | sort -n >sellDiff3
cat a3 | sed -e "s/^.*spreadBuyDiff=//" -e "s/,.*//" | sort -n >buyDiff3

