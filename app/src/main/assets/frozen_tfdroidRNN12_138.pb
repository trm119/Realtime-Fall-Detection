
C
InputPlaceholder*
dtype0* 
shape:���������K
2
rnn/RankConst*
value	B :*
dtype0
9
rnn/range/startConst*
value	B :*
dtype0
9
rnn/range/deltaConst*
value	B :*
dtype0
J
	rnn/rangeRangernn/range/startrnn/Rankrnn/range/delta*

Tidx0
H
rnn/concat/values_0Const*
valueB"       *
dtype0
9
rnn/concat/axisConst*
value	B : *
dtype0
e

rnn/concatConcatV2rnn/concat/values_0	rnn/rangernn/concat/axis*
T0*
N*

Tidx0
C
rnn/transpose	TransposeInput
rnn/concat*
T0*
Tperm0
:
	rnn/ShapeShapernn/transpose*
out_type0*
T0
E
rnn/strided_slice/stackConst*
valueB:*
dtype0
G
rnn/strided_slice/stack_1Const*
valueB:*
dtype0
G
rnn/strided_slice/stack_2Const*
valueB:*
dtype0
�
rnn/strided_sliceStridedSlice	rnn/Shapernn/strided_slice/stackrnn/strided_slice/stack_1rnn/strided_slice/stack_2*
Index0*
end_mask *
shrink_axis_mask*
T0*
new_axis_mask *

begin_mask *
ellipsis_mask 
h
>rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/ExpandDims/dimConst*
value	B : *
dtype0
�
:rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/ExpandDims
ExpandDimsrnn/strided_slice>rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/ExpandDims/dim*

Tdim0*
T0
c
5rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/ConstConst*
valueB:*
dtype0
e
;rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/concat/axisConst*
value	B : *
dtype0
�
6rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/concatConcatV2:rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/ExpandDims5rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/Const;rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/concat/axis*
T0*
N*

Tidx0
h
;rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/zeros/ConstConst*
valueB
 *    *
dtype0
�
5rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/zerosFill6rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/concat;rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/zeros/Const*
T0*

index_type0
<
rnn/Shape_1Shapernn/transpose*
out_type0*
T0
G
rnn/strided_slice_1/stackConst*
valueB: *
dtype0
I
rnn/strided_slice_1/stack_1Const*
valueB:*
dtype0
I
rnn/strided_slice_1/stack_2Const*
valueB:*
dtype0
�
rnn/strided_slice_1StridedSlicernn/Shape_1rnn/strided_slice_1/stackrnn/strided_slice_1/stack_1rnn/strided_slice_1/stack_2*
Index0*
end_mask *
shrink_axis_mask*
T0*
new_axis_mask *

begin_mask *
ellipsis_mask 
2
rnn/timeConst*
value	B : *
dtype0
�
rnn/TensorArray_1TensorArrayV3rnn/strided_slice_1*
dynamic_size( *
identical_element_shapes(*$
element_shape:���������*
dtype0*.
tensor_array_namernn/dynamic_rnn/input_0*
clear_after_read(
M
rnn/TensorArrayUnstack/ShapeShapernn/transpose*
out_type0*
T0
X
*rnn/TensorArrayUnstack/strided_slice/stackConst*
valueB: *
dtype0
Z
,rnn/TensorArrayUnstack/strided_slice/stack_1Const*
valueB:*
dtype0
Z
,rnn/TensorArrayUnstack/strided_slice/stack_2Const*
valueB:*
dtype0
�
$rnn/TensorArrayUnstack/strided_sliceStridedSlicernn/TensorArrayUnstack/Shape*rnn/TensorArrayUnstack/strided_slice/stack,rnn/TensorArrayUnstack/strided_slice/stack_1,rnn/TensorArrayUnstack/strided_slice/stack_2*
Index0*
end_mask *
shrink_axis_mask*
T0*
new_axis_mask *

begin_mask *
ellipsis_mask 
L
"rnn/TensorArrayUnstack/range/startConst*
value	B : *
dtype0
L
"rnn/TensorArrayUnstack/range/deltaConst*
value	B :*
dtype0
�
rnn/TensorArrayUnstack/rangeRange"rnn/TensorArrayUnstack/range/start$rnn/TensorArrayUnstack/strided_slice"rnn/TensorArrayUnstack/range/delta*

Tidx0
�
>rnn/TensorArrayUnstack/TensorArrayScatter/TensorArrayScatterV3TensorArrayScatterV3rnn/TensorArray_1rnn/TensorArrayUnstack/rangernn/transposernn/TensorArray_1:1*
T0* 
_class
loc:@rnn/transpose
7
rnn/Maximum/xConst*
value	B :*
dtype0
C
rnn/MaximumMaximumrnn/Maximum/xrnn/strided_slice_1*
T0
A
rnn/MinimumMinimumrnn/strided_slice_1rnn/Maximum*
T0
E
rnn/while/iteration_counterConst*
value	B : *
dtype0
�
rnn/while/EnterEnterrnn/while/iteration_counter*
parallel_iterations *
T0*
is_constant( *'

frame_namernn/while/while_context
�
rnn/while/Enter_1Enterrnn/time*
parallel_iterations *
T0*
is_constant( *'

frame_namernn/while/while_context
�
rnn/while/Enter_3Enter5rnn/MultiRNNCellZeroState/MultiRNNCellZeroState/zeros*
parallel_iterations *
T0*
is_constant( *'

frame_namernn/while/while_context
T
rnn/while/MergeMergernn/while/Enterrnn/while/NextIteration*
T0*
N
Z
rnn/while/Merge_1Mergernn/while/Enter_1rnn/while/NextIteration_1*
T0*
N
Z
rnn/while/Merge_3Mergernn/while/Enter_3rnn/while/NextIteration_3*
T0*
N
F
rnn/while/LessLessrnn/while/Mergernn/while/Less/Enter*
T0
�
rnn/while/Less/EnterEnterrnn/strided_slice_1*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
L
rnn/while/Less_1Lessrnn/while/Merge_1rnn/while/Less_1/Enter*
T0
�
rnn/while/Less_1/EnterEnterrnn/Minimum*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
D
rnn/while/LogicalAnd
LogicalAndrnn/while/Lessrnn/while/Less_1
4
rnn/while/LoopCondLoopCondrnn/while/LogicalAnd
l
rnn/while/SwitchSwitchrnn/while/Mergernn/while/LoopCond*
T0*"
_class
loc:@rnn/while/Merge
r
rnn/while/Switch_1Switchrnn/while/Merge_1rnn/while/LoopCond*
T0*$
_class
loc:@rnn/while/Merge_1
r
rnn/while/Switch_3Switchrnn/while/Merge_3rnn/while/LoopCond*
T0*$
_class
loc:@rnn/while/Merge_3
;
rnn/while/IdentityIdentityrnn/while/Switch:1*
T0
?
rnn/while/Identity_1Identityrnn/while/Switch_1:1*
T0
?
rnn/while/Identity_3Identityrnn/while/Switch_3:1*
T0
N
rnn/while/add/yConst^rnn/while/Identity*
value	B :*
dtype0
B
rnn/while/addAddrnn/while/Identityrnn/while/add/y*
T0
�
rnn/while/TensorArrayReadV3TensorArrayReadV3!rnn/while/TensorArrayReadV3/Enterrnn/while/Identity_1#rnn/while/TensorArrayReadV3/Enter_1*
dtype0
�
!rnn/while/TensorArrayReadV3/EnterEnterrnn/TensorArray_1*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
�
#rnn/while/TensorArrayReadV3/Enter_1Enter>rnn/TensorArrayUnstack/TensorArrayScatter/TensorArrayScatterV3*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
y
/rnn/while/rnn/multi_rnn_cell/cell_0/Slice/beginConst^rnn/while/Identity*
valueB"        *
dtype0
x
.rnn/while/rnn/multi_rnn_cell/cell_0/Slice/sizeConst^rnn/while/Identity*
valueB"����   *
dtype0
�
)rnn/while/rnn/multi_rnn_cell/cell_0/SliceSlicernn/while/Identity_3/rnn/while/rnn/multi_rnn_cell/cell_0/Slice/begin.rnn/while/rnn/multi_rnn_cell/cell_0/Slice/size*
Index0*
T0
�
/rnn/multi_rnn_cell/cell_0/gru_cell/gates/kernelConst*�
value�B�"���V>`���+'>�>�I��_8>*�)�kX��Î�������滼1�>׼�=�m�=h��d�2>6p:�-��46?�'$?jY�>���hP>a�~=�"�񦈾r�>��X�?�c����>�3���>vy�>aa��]9�>�K��s$��08������.�%��>v�C>Ǽ�>&�F> ��=_rȾ]��>��>�>.;�=2��>��>�q=��`��,�ʳg=��A>�,����=�% ��e��\�>�.�=��:�b�����4�=��b>\F�=Sc=Ǥ�K�νm��=���=
�����>CY�>�����$�.�Z>�Io=Q�;6JǾd6׽���Xv�=��k�����l��>,p����$�z�O�[=�K >tn>��E����<�F����)B�=%�>+���c��>��=߇�>Yw%>���>�^����ջ�B����i�=$�ULԾ�^�>�^ϻ�7> Dt����=�ˆ>-�>��{��!E>��7��?�s=��%����>��e>&I��n�!A�����{ھ=�d>�sa>�Ia�3�>�>u�Խ��	���>Bdc��ٽm��>��`>��>�\>�5�>�鑾@S�<��J��m�>��̽t�%>4�ľNW,��<O��$4>:W���S�>Չ��e$=����,���&>>��>�pk=�&�=<4=��>��h>��)��=8·>֏*>���>	��="�ֽ@�>��'�S��=ꮗ>'Y+�p��=򌚾���=��>y��>C�4>"� �x�=�#]��-�>�5?7�{>_}?9V����=Ԫ�:4M=,E?���>;x��4�qh*>��<����=��;�;_>�h�>�v���t?�X�=��?wsL>��>�D�>�m<Pp�>����=��}�>���>[i�>-rw>с�>���=��~�ߊ��k�>�8s��(�>�|#?��q>��Z=��R������q����Y2�=_�>�:>O<���J��@%>94���R'=���>��>v^��G>�|<�����N��}�E�kŧ�"�����d���Q��>_��>x+��wU�<b��>��X>��*>ʰ��`�׽^��>E'n�,��>��`=ܓ�=�����U �ڊX���K��tE>~�
?��$�۾��M�*Z>�w���9=��<m"�>+�}>n��{�>�*���>�Ci���b>א�=���=kF¾��=ud >V�,����>�2ؽ��4>�x;>�l���䫾+��>�0�>�$��$N�>���={��>�Z������ޗ>�`=�o>����F>-*��3�<i�<V�=�>�N���~>MY=�T���C�>�A�>|���#���?>iC�����?�=W>�>i��>�i2�Ͳ�>1��lj,>�=+z����>�>1�=�Uɾm9��މ>�T>%W��+QK��"y>��ܽ�C_�*
dtype0
z
4rnn/multi_rnn_cell/cell_0/gru_cell/gates/kernel/readIdentity/rnn/multi_rnn_cell/cell_0/gru_cell/gates/kernel*
T0
�
-rnn/multi_rnn_cell/cell_0/gru_cell/gates/biasConst*u
valuelBj"`��?ɫ�?�l�?>�}?ҡ�?i�?z��?Y`?�Q�?_��?�?�$w?�?�v?�1?@,j?�ˍ?��a?��G?�{?��?)%�?�̖?z�?*
dtype0
v
2rnn/multi_rnn_cell/cell_0/gru_cell/gates/bias/readIdentity-rnn/multi_rnn_cell/cell_0/gru_cell/gates/bias*
T0
�
3rnn/multi_rnn_cell/cell_0/gru_cell/candidate/kernelConst*�
value�B�"��=�`=�Vp>�m�>#��=������	��^�=&n��4>���=y�>�Qm�v�����>���7*?򦣾*{��ƽ�Ǭ� ��Y�>7��>@j�>��9��#?>���>�o1?`c?���>iʤ>U�a�>]��=���>2���XK�Y�S���>�<w�}Sy>j�ʾ8ξ-��<J��>�|������>K6ȼ���@�>-w#>��ݽj�?z,���x_>橾��e>��>h�>��*�W�o��Z����?|/>��]>�k�>�a����3�5>��|>(m�=���=hż-`�>S1H>c8
<݀�=�n ?2�{<���>�%��o�;�SL�=���>쩾Y�>�3�be��*��>7nQ��g�>��=���>� 㽙y����>$ >b��>Gm�>,��fP�>0?>w�>�?���a��!��=4$l�W?���>�����iS��]��{�>���>����q`�R@�ހ��J>�L�=�>�����%e�>}�=�/>��־��>�Ұ>�X��>�?��>�ÿ<r����>���>>uxz>��>^��>&\��8�>xJ*=@Y>:x�>K`*���>�ʈ>��>2��,>�D���W���P?k ��1߼����&>���>�&���>Mz��0�>j�>��>؊�>�%����>6�>^�=Cs����#>2��x��s�>�e��*
dtype0
�
8rnn/multi_rnn_cell/cell_0/gru_cell/candidate/kernel/readIdentity3rnn/multi_rnn_cell/cell_0/gru_cell/candidate/kernel*
T0
�
1rnn/multi_rnn_cell/cell_0/gru_cell/candidate/biasConst*E
value<B:"0��=��;*S����>�_E��
=�W8=�đ<�k�=�}=V�<?�9;*
dtype0
~
6rnn/multi_rnn_cell/cell_0/gru_cell/candidate/bias/readIdentity1rnn/multi_rnn_cell/cell_0/gru_cell/candidate/bias*
T0
w
8rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat/axisConst^rnn/while/Identity*
value	B :*
dtype0
�
3rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concatConcatV2rnn/while/TensorArrayReadV3)rnn/while/rnn/multi_rnn_cell/cell_0/Slice8rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat/axis*
T0*
N*

Tidx0
�
3rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMulMatMul3rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat9rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul/Enter*
transpose_b( *
T0*
transpose_a( 
�
9rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul/EnterEnter4rnn/multi_rnn_cell/cell_0/gru_cell/gates/kernel/read*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
�
4rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAddBiasAdd3rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul:rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd/Enter*
T0*
data_formatNHWC
�
:rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd/EnterEnter2rnn/multi_rnn_cell/cell_0/gru_cell/gates/bias/read*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
~
4rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/SigmoidSigmoid4rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd*
T0
{
<rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/split/split_dimConst^rnn/while/Identity*
value	B :*
dtype0
�
2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/splitSplit<rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/split/split_dim4rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/Sigmoid*
T0*
	num_split
�
0rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/mulMul2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/split)rnn/while/rnn/multi_rnn_cell/cell_0/Slice*
T0
y
:rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat_1/axisConst^rnn/while/Identity*
value	B :*
dtype0
�
5rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat_1ConcatV2rnn/while/TensorArrayReadV30rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/mul:rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat_1/axis*
T0*
N*

Tidx0
�
5rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul_1MatMul5rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/concat_1;rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul_1/Enter*
transpose_b( *
T0*
transpose_a( 
�
;rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul_1/EnterEnter8rnn/multi_rnn_cell/cell_0/gru_cell/candidate/kernel/read*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
�
6rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd_1BiasAdd5rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/MatMul_1<rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd_1/Enter*
T0*
data_formatNHWC
�
<rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd_1/EnterEnter6rnn/multi_rnn_cell/cell_0/gru_cell/candidate/bias/read*
parallel_iterations *
T0*
is_constant(*'

frame_namernn/while/while_context
z
1rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/ReluRelu6rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/BiasAdd_1*
T0
�
2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/mul_1Mul4rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/split:1)rnn/while/rnn/multi_rnn_cell/cell_0/Slice*
T0
t
2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/sub/xConst^rnn/while/Identity*
valueB
 *  �?*
dtype0
�
0rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/subSub2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/sub/x4rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/split:1*
T0
�
2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/mul_2Mul0rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/sub1rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/Relu*
T0
�
0rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/addAdd2rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/mul_12rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/mul_2*
T0
j
#rnn/while/rnn/multi_rnn_cell/concatIdentity0rnn/while/rnn/multi_rnn_cell/cell_0/gru_cell/add*
T0
P
rnn/while/add_1/yConst^rnn/while/Identity*
value	B :*
dtype0
H
rnn/while/add_1Addrnn/while/Identity_1rnn/while/add_1/y*
T0
@
rnn/while/NextIterationNextIterationrnn/while/add*
T0
D
rnn/while/NextIteration_1NextIterationrnn/while/add_1*
T0
X
rnn/while/NextIteration_3NextIteration#rnn/while/rnn/multi_rnn_cell/concat*
T0
5
rnn/while/Exit_3Exitrnn/while/Switch_3*
T0
�
dense/kernelConst*y
valuepBn"`ѐ�=gBͽK��J��>-�>�X>���*��#>�?i�z��uT?�V��b]��е�[#?�V�=�Ӕ��"�>��N�>79?��q:Sz8=*
dtype0
U
dense/kernel/readIdentitydense/kernel*
T0*
_class
loc:@dense/kernel
?

dense/biasConst*
valueB"	X;�X�*
dtype0
O
dense/bias/readIdentity
dense/bias*
T0*
_class
loc:@dense/bias
j
dense/MatMulMatMulrnn/while/Exit_3dense/kernel/read*
transpose_b( *
T0*
transpose_a( 
W
dense/BiasAddBiasAdddense/MatMuldense/bias/read*
T0*
data_formatNHWC
)
OutputSoftmaxdense/BiasAdd*
T0 