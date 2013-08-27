/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class SqlLexer extends AbstractLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\6\1\3\1\2\1\0\1\3\1\1\16\6\4\0\1\52\1\51"+
    "\1\15\1\0\1\5\2\0\1\16\1\51\1\51\1\0\1\14\1\51"+
    "\1\4\1\12\1\0\1\7\3\10\1\46\3\10\1\46\1\10\1\51"+
    "\1\51\1\51\1\51\1\51\1\51\1\51\1\20\1\32\1\31\1\21"+
    "\1\13\1\11\1\36\1\37\1\35\1\47\1\40\1\22\1\42\1\25"+
    "\1\33\1\44\1\50\1\24\1\30\1\23\1\41\1\43\1\34\1\45"+
    "\1\26\1\27\1\51\1\17\1\51\1\0\1\5\1\0\1\20\1\32"+
    "\1\31\1\21\1\13\1\11\1\36\1\37\1\35\1\47\1\40\1\22"+
    "\1\42\1\25\1\33\1\44\1\50\1\24\1\30\1\23\1\41\1\43"+
    "\1\34\1\45\1\26\1\27\1\51\1\0\1\51\1\51\41\6\2\0"+
    "\4\5\4\0\1\5\2\0\1\6\7\0\1\5\4\0\1\5\5\0"+
    "\27\5\1\0\37\5\1\0\u013f\5\31\0\162\5\4\0\14\5\16\0"+
    "\5\5\11\0\1\5\21\0\130\6\5\0\23\6\12\0\1\5\13\0"+
    "\1\5\1\0\3\5\1\0\1\5\1\0\24\5\1\0\54\5\1\0"+
    "\46\5\1\0\5\5\4\0\202\5\1\0\4\6\3\0\105\5\1\0"+
    "\46\5\2\0\2\5\6\0\20\5\41\0\46\5\2\0\1\5\7\0"+
    "\47\5\11\0\21\6\1\0\27\6\1\0\3\6\1\0\1\6\1\0"+
    "\2\6\1\0\1\6\13\0\33\5\5\0\3\5\15\0\4\6\14\0"+
    "\6\6\13\0\32\5\5\0\13\5\16\6\7\0\12\6\4\0\2\5"+
    "\1\6\143\5\1\0\1\5\10\6\1\0\6\6\2\5\2\6\1\0"+
    "\4\6\2\5\12\6\3\5\2\0\1\5\17\0\1\6\1\5\1\6"+
    "\36\5\33\6\2\0\3\5\60\0\46\5\13\6\1\5\u014f\0\3\6"+
    "\66\5\2\0\1\6\1\5\20\6\2\0\1\5\4\6\3\0\12\5"+
    "\2\6\2\0\12\6\21\0\3\6\1\0\10\5\2\0\2\5\2\0"+
    "\26\5\1\0\7\5\1\0\1\5\3\0\4\5\2\0\1\6\1\5"+
    "\7\6\2\0\2\6\2\0\3\6\11\0\1\6\4\0\2\5\1\0"+
    "\3\5\2\6\2\0\12\6\4\5\15\0\3\6\1\0\6\5\4\0"+
    "\2\5\2\0\26\5\1\0\7\5\1\0\2\5\1\0\2\5\1\0"+
    "\2\5\2\0\1\6\1\0\5\6\4\0\2\6\2\0\3\6\13\0"+
    "\4\5\1\0\1\5\7\0\14\6\3\5\14\0\3\6\1\0\11\5"+
    "\1\0\3\5\1\0\26\5\1\0\7\5\1\0\2\5\1\0\5\5"+
    "\2\0\1\6\1\5\10\6\1\0\3\6\1\0\3\6\2\0\1\5"+
    "\17\0\2\5\2\6\2\0\12\6\1\0\1\5\17\0\3\6\1\0"+
    "\10\5\2\0\2\5\2\0\26\5\1\0\7\5\1\0\2\5\1\0"+
    "\5\5\2\0\1\6\1\5\6\6\3\0\2\6\2\0\3\6\10\0"+
    "\2\6\4\0\2\5\1\0\3\5\4\0\12\6\1\0\1\5\20\0"+
    "\1\6\1\5\1\0\6\5\3\0\3\5\1\0\4\5\3\0\2\5"+
    "\1\0\1\5\1\0\2\5\3\0\2\5\3\0\3\5\3\0\10\5"+
    "\1\0\3\5\4\0\5\6\3\0\3\6\1\0\4\6\11\0\1\6"+
    "\17\0\11\6\11\0\1\5\7\0\3\6\1\0\10\5\1\0\3\5"+
    "\1\0\27\5\1\0\12\5\1\0\5\5\4\0\7\6\1\0\3\6"+
    "\1\0\4\6\7\0\2\6\11\0\2\5\4\0\12\6\22\0\2\6"+
    "\1\0\10\5\1\0\3\5\1\0\27\5\1\0\12\5\1\0\5\5"+
    "\2\0\1\6\1\5\7\6\1\0\3\6\1\0\4\6\7\0\2\6"+
    "\7\0\1\5\1\0\2\5\4\0\12\6\22\0\2\6\1\0\10\5"+
    "\1\0\3\5\1\0\27\5\1\0\20\5\4\0\6\6\2\0\3\6"+
    "\1\0\4\6\11\0\1\6\10\0\2\5\4\0\12\6\22\0\2\6"+
    "\1\0\22\5\3\0\30\5\1\0\11\5\1\0\1\5\2\0\7\5"+
    "\3\0\1\6\4\0\6\6\1\0\1\6\1\0\10\6\22\0\2\6"+
    "\15\0\60\5\1\6\2\5\7\6\4\0\10\5\10\6\1\0\12\6"+
    "\47\0\2\5\1\0\1\5\2\0\2\5\1\0\1\5\2\0\1\5"+
    "\6\0\4\5\1\0\7\5\1\0\3\5\1\0\1\5\1\0\1\5"+
    "\2\0\2\5\1\0\4\5\1\6\2\5\6\6\1\0\2\6\1\5"+
    "\2\0\5\5\1\0\1\5\1\0\6\6\2\0\12\6\2\0\2\5"+
    "\42\0\1\5\27\0\2\6\6\0\12\6\13\0\1\6\1\0\1\6"+
    "\1\0\1\6\4\0\2\6\10\5\1\0\42\5\6\0\24\6\1\0"+
    "\2\6\4\5\4\0\10\6\1\0\44\6\11\0\1\6\71\0\42\5"+
    "\1\0\5\5\1\0\2\5\1\0\7\6\3\0\4\6\6\0\12\6"+
    "\6\0\6\5\4\6\106\0\46\5\12\0\51\5\7\0\132\5\5\0"+
    "\104\5\5\0\122\5\6\0\7\5\1\0\77\5\1\0\1\5\1\0"+
    "\4\5\2\0\7\5\1\0\1\5\1\0\4\5\2\0\47\5\1\0"+
    "\1\5\1\0\4\5\2\0\37\5\1\0\1\5\1\0\4\5\2\0"+
    "\7\5\1\0\1\5\1\0\4\5\2\0\7\5\1\0\7\5\1\0"+
    "\27\5\1\0\37\5\1\0\1\5\1\0\4\5\2\0\7\5\1\0"+
    "\47\5\1\0\23\5\16\0\11\6\56\0\125\5\14\0\u026c\5\2\0"+
    "\10\5\12\0\32\5\5\0\113\5\3\0\3\5\17\0\15\5\1\0"+
    "\4\5\3\6\13\0\22\5\3\6\13\0\22\5\2\6\14\0\15\5"+
    "\1\0\3\5\1\0\2\6\14\0\64\5\40\6\3\0\1\5\3\0"+
    "\2\5\1\6\2\0\12\6\41\0\3\6\2\0\12\6\6\0\130\5"+
    "\10\0\51\5\1\6\126\0\35\5\3\0\14\6\4\0\14\6\12\0"+
    "\12\6\36\5\2\0\5\5\u038b\0\154\5\224\0\234\5\4\0\132\5"+
    "\6\0\26\5\2\0\6\5\2\0\46\5\2\0\6\5\2\0\10\5"+
    "\1\0\1\5\1\0\1\5\1\0\1\5\1\0\37\5\2\0\65\5"+
    "\1\0\7\5\1\0\1\5\3\0\3\5\1\0\7\5\3\0\4\5"+
    "\2\0\6\5\4\0\15\5\5\0\3\5\1\0\7\5\17\0\4\6"+
    "\32\0\5\6\20\0\2\5\23\0\1\5\13\0\4\6\6\0\6\6"+
    "\1\0\1\5\15\0\1\5\40\0\22\5\36\0\15\6\4\0\1\6"+
    "\3\0\6\6\27\0\1\5\4\0\1\5\2\0\12\5\1\0\1\5"+
    "\3\0\5\5\6\0\1\5\1\0\1\5\1\0\1\5\1\0\4\5"+
    "\1\0\3\5\1\0\7\5\3\0\3\5\5\0\5\5\26\0\44\5"+
    "\u0e81\0\3\5\31\0\11\5\6\6\1\0\5\5\2\0\5\5\4\0"+
    "\126\5\2\0\2\6\2\0\3\5\1\0\137\5\5\0\50\5\4\0"+
    "\136\5\21\0\30\5\70\0\20\5\u0200\0\u19b6\5\112\0\u51a6\5\132\0"+
    "\u048d\5\u0773\0\u2ba4\5\u215c\0\u012e\5\2\0\73\5\225\0\7\5\14\0"+
    "\5\5\5\0\1\5\1\6\12\5\1\0\15\5\1\0\5\5\1\0"+
    "\1\5\1\0\2\5\1\0\2\5\1\0\154\5\41\0\u016b\5\22\0"+
    "\100\5\2\0\66\5\50\0\15\5\3\0\20\6\20\0\4\6\17\0"+
    "\2\5\30\0\3\5\31\0\1\5\6\0\5\5\1\0\207\5\2\0"+
    "\1\6\4\0\1\5\13\0\12\6\7\0\32\5\4\0\1\5\1\0"+
    "\32\5\12\0\132\5\3\0\6\5\2\0\6\5\2\0\6\5\2\0"+
    "\3\5\3\0\2\5\3\0\2\5\22\0\3\6\4\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\1\1\3\2\4\1\3\1\5"+
    "\1\3\2\1\26\3\1\5\1\6\1\0\1\4\2\0"+
    "\13\3\2\0\3\3\1\7\14\3\1\7\31\3\1\7"+
    "\5\3\1\7\16\3\2\6\2\0\3\3\1\7\7\3"+
    "\2\10\6\3\1\7\50\3\1\7\15\3\1\7\6\3"+
    "\1\7\5\3\1\7\6\3\1\7\10\3\1\7\10\3"+
    "\1\7\46\3\1\7\16\3\1\7\66\3\1\7\14\3"+
    "\1\7\3\3";

  private static int [] zzUnpackAction() {
    int [] result = new int[358];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\53\0\126\0\201\0\254\0\327\0\u0102\0\u012d"+
    "\0\u0158\0\u0183\0\u01ae\0\u01d9\0\u0204\0\u022f\0\u025a\0\u0285"+
    "\0\u02b0\0\u02db\0\u0306\0\u0331\0\u035c\0\u0387\0\u03b2\0\u03dd"+
    "\0\u0408\0\u0433\0\u045e\0\u0489\0\u04b4\0\u04df\0\u050a\0\u0535"+
    "\0\u0560\0\u058b\0\53\0\u05b6\0\327\0\53\0\u05e1\0\u060c"+
    "\0\u0637\0\u0662\0\u068d\0\u06b8\0\u06e3\0\u070e\0\u0739\0\u0764"+
    "\0\u078f\0\u07ba\0\u07e5\0\u0810\0\u083b\0\u0866\0\u0891\0\u08bc"+
    "\0\u08e7\0\u0912\0\u093d\0\u0968\0\u0993\0\u09be\0\u09e9\0\u0a14"+
    "\0\u0a3f\0\u0a6a\0\u0a95\0\u0ac0\0\u0aeb\0\254\0\u0b16\0\u0b41"+
    "\0\u0b6c\0\u0b97\0\u0bc2\0\u0bed\0\u0c18\0\u0c43\0\u0c6e\0\u0c99"+
    "\0\u0cc4\0\u0cef\0\u0d1a\0\u0d45\0\u0d70\0\u0d9b\0\u0dc6\0\u0df1"+
    "\0\u0e1c\0\u0e47\0\u0e72\0\u0e9d\0\u0ec8\0\u0ef3\0\u0f1e\0\u0f49"+
    "\0\u0f74\0\u0f9f\0\u0fca\0\u0ff5\0\u1020\0\u104b\0\u1076\0\u10a1"+
    "\0\u10cc\0\u10f7\0\u1122\0\u114d\0\u1178\0\u11a3\0\u11ce\0\u11f9"+
    "\0\u1224\0\u124f\0\u127a\0\u12a5\0\u12d0\0\53\0\u12fb\0\u1326"+
    "\0\u1351\0\u137c\0\u13a7\0\u13d2\0\u13fd\0\u1428\0\u1453\0\u147e"+
    "\0\u14a9\0\u14d4\0\u14ff\0\53\0\u083b\0\u152a\0\u1555\0\u1580"+
    "\0\u15ab\0\u15d6\0\u1601\0\u162c\0\u1657\0\u1682\0\u16ad\0\u16d8"+
    "\0\u1703\0\u172e\0\u1759\0\u1784\0\u17af\0\u17da\0\u1805\0\u1830"+
    "\0\u185b\0\u1886\0\u18b1\0\u18dc\0\u1907\0\u1932\0\u195d\0\u1988"+
    "\0\u19b3\0\u19de\0\u1a09\0\u1a34\0\u1a5f\0\u1a8a\0\u1ab5\0\u1ae0"+
    "\0\u1b0b\0\u1b36\0\u1b61\0\u1b8c\0\u1bb7\0\u1be2\0\u1c0d\0\u1c38"+
    "\0\u1c63\0\u1c8e\0\u1cb9\0\u1ce4\0\u1d0f\0\u1d3a\0\u1d65\0\u1d90"+
    "\0\u1dbb\0\u1de6\0\u1e11\0\u1e3c\0\u1e67\0\u1e92\0\u1ebd\0\u1ee8"+
    "\0\u1f13\0\u1f3e\0\u1f69\0\u1f94\0\u1fbf\0\u1fea\0\u2015\0\u2040"+
    "\0\u206b\0\u2096\0\u20c1\0\u20ec\0\u2117\0\u2142\0\u216d\0\u2198"+
    "\0\u21c3\0\u21ee\0\u2219\0\u2244\0\u226f\0\u229a\0\u22c5\0\u22f0"+
    "\0\u231b\0\u2346\0\u2371\0\u239c\0\u23c7\0\u23f2\0\u241d\0\u2448"+
    "\0\u2473\0\u249e\0\u24c9\0\u24f4\0\u251f\0\u254a\0\u2575\0\u25a0"+
    "\0\u25cb\0\u25f6\0\u2621\0\u264c\0\u2677\0\u2198\0\u26a2\0\u26cd"+
    "\0\u26f8\0\u2723\0\u274e\0\u2779\0\u27a4\0\u27cf\0\u27fa\0\u2825"+
    "\0\u2850\0\u287b\0\u28a6\0\u28d1\0\u28fc\0\u2927\0\u2952\0\u297d"+
    "\0\u29a8\0\u29d3\0\u29fe\0\u2a29\0\u2a54\0\u2a7f\0\u2aaa\0\u2ad5"+
    "\0\u2b00\0\u2b2b\0\u2b56\0\u2b81\0\u2bac\0\u2bd7\0\u2c02\0\u2c2d"+
    "\0\u2c58\0\u2c83\0\u2cae\0\u2cd9\0\u2d04\0\u2d2f\0\u2d5a\0\u2d85"+
    "\0\u2db0\0\u2ddb\0\u2e06\0\u2e31\0\u2e5c\0\u2e87\0\u2eb2\0\u2edd"+
    "\0\u2f08\0\u2f33\0\u2f5e\0\u2f89\0\u2fb4\0\u2fdf\0\u300a\0\u3035"+
    "\0\u3060\0\u308b\0\u30b6\0\u30e1\0\u310c\0\u3137\0\u3162\0\u318d"+
    "\0\u31b8\0\u31e3\0\u320e\0\u3239\0\u3264\0\u328f\0\u32ba\0\u32e5"+
    "\0\u3310\0\u333b\0\u3366\0\u3391\0\u33bc\0\u33e7\0\u3412\0\u343d"+
    "\0\u3468\0\u3493\0\u34be\0\u34e9\0\u3514\0\u353f\0\u356a\0\u3595"+
    "\0\u35c0\0\u35eb\0\u3616\0\u3641\0\u366c\0\u08e7\0\u3697\0\u36c2"+
    "\0\u36ed\0\u3718\0\u3743\0\u376e\0\u3799\0\u37c4\0\u37ef\0\u381a"+
    "\0\u3845\0\u3870\0\u389b\0\u38c6\0\u38f1\0\u391c\0\u3947\0\u3972"+
    "\0\u399d\0\u39c8\0\u39f3\0\u3a1e\0\u3a49\0\u3a74";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[358];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\3\3\1\4\1\5\1\2\1\6\1\7\1\10"+
    "\1\11\1\12\1\2\1\13\1\14\1\2\1\15\1\16"+
    "\1\17\1\20\1\21\1\22\1\5\1\23\1\24\1\25"+
    "\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\35"+
    "\1\36\1\37\1\40\1\41\1\7\1\42\1\5\1\43"+
    "\1\3\54\0\3\3\46\0\1\3\4\0\1\44\53\0"+
    "\5\5\1\0\1\5\4\0\31\5\11\0\2\45\1\46"+
    "\1\47\1\50\32\0\1\45\13\0\2\7\1\46\1\47"+
    "\1\50\32\0\1\7\11\0\5\5\1\0\1\51\4\0"+
    "\1\52\1\5\1\53\1\5\1\54\6\5\1\55\5\5"+
    "\1\56\7\5\11\0\2\47\35\0\1\47\11\0\5\5"+
    "\1\0\1\5\4\0\1\57\1\5\1\60\2\5\1\61"+
    "\2\5\1\62\14\5\1\63\3\5\2\0\1\64\2\0"+
    "\12\64\1\0\1\64\1\0\33\64\1\65\2\0\13\65"+
    "\2\0\33\65\5\0\5\5\1\0\1\5\4\0\1\5"+
    "\1\66\1\67\2\5\1\70\2\5\1\71\20\5\7\0"+
    "\5\5\1\0\1\72\4\0\1\73\3\5\1\74\6\5"+
    "\1\75\1\5\1\76\3\5\1\77\7\5\7\0\5\5"+
    "\1\0\1\100\4\0\13\5\1\101\1\5\1\102\13\5"+
    "\7\0\5\5\1\0\1\103\4\0\1\104\3\5\1\105"+
    "\6\5\1\106\3\5\1\107\11\5\7\0\5\5\1\0"+
    "\1\110\4\0\2\5\1\111\12\5\1\112\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\113\12\5\1\114\5\5"+
    "\1\115\7\5\7\0\5\5\1\0\1\116\4\0\31\5"+
    "\7\0\5\5\1\0\1\117\4\0\3\5\1\120\5\5"+
    "\1\121\1\5\1\122\3\5\1\123\2\5\1\124\1\5"+
    "\1\125\3\5\1\126\7\0\5\5\1\0\1\5\4\0"+
    "\1\127\3\5\1\130\6\5\1\131\3\5\1\132\1\5"+
    "\1\133\7\5\7\0\5\5\1\0\1\134\4\0\2\5"+
    "\1\135\3\5\1\106\4\5\1\136\1\5\1\137\13\5"+
    "\7\0\5\5\1\0\1\5\4\0\4\5\1\140\1\106"+
    "\13\5\1\141\2\5\1\142\4\5\7\0\5\5\1\0"+
    "\1\5\4\0\4\5\1\143\10\5\1\136\1\5\1\144"+
    "\11\5\7\0\4\5\1\106\1\0\1\5\4\0\3\5"+
    "\1\145\1\5\1\146\2\5\1\106\5\5\1\147\12\5"+
    "\7\0\5\5\1\0\1\5\4\0\4\5\1\150\24\5"+
    "\7\0\5\5\1\0\1\5\4\0\1\151\30\5\7\0"+
    "\5\5\1\0\1\152\4\0\15\5\1\153\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\5\5\1\154\2\5\1\155"+
    "\13\5\1\156\4\5\7\0\5\5\1\0\1\157\4\0"+
    "\1\51\12\5\1\160\15\5\7\0\5\5\1\0\1\5"+
    "\4\0\1\161\30\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\162\14\5\1\157\7\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\163\15\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\164\15\5\2\0\1\44\1\165"+
    "\1\166\50\44\7\0\2\47\1\46\1\0\1\50\32\0"+
    "\1\47\10\0\1\167\2\0\2\170\3\0\1\167\31\0"+
    "\1\170\11\0\5\5\1\0\1\5\4\0\3\5\1\57"+
    "\25\5\7\0\5\5\1\0\1\5\4\0\2\5\1\171"+
    "\26\5\7\0\5\5\1\0\1\5\4\0\13\5\1\172"+
    "\15\5\7\0\5\5\1\0\1\5\4\0\13\5\1\173"+
    "\15\5\7\0\5\5\1\0\1\5\4\0\4\5\1\174"+
    "\24\5\7\0\5\5\1\0\1\5\4\0\2\5\1\175"+
    "\26\5\7\0\5\5\1\0\1\5\4\0\11\5\1\176"+
    "\17\5\7\0\5\5\1\0\1\5\4\0\10\5\1\177"+
    "\20\5\7\0\5\5\1\0\1\5\4\0\11\5\1\200"+
    "\17\5\7\0\5\5\1\0\1\5\4\0\11\5\1\201"+
    "\17\5\7\0\5\5\1\0\1\5\4\0\15\5\1\202"+
    "\6\5\1\203\4\5\2\0\1\64\2\0\12\64\1\204"+
    "\1\64\1\0\33\64\1\65\2\0\13\65\2\0\32\65"+
    "\1\205\5\0\5\5\1\0\1\5\4\0\1\5\1\106"+
    "\27\5\7\0\5\5\1\0\1\5\4\0\2\5\1\106"+
    "\1\206\25\5\7\0\5\5\1\0\1\5\4\0\1\207"+
    "\1\106\27\5\7\0\5\5\1\0\1\5\4\0\11\5"+
    "\1\106\17\5\7\0\4\5\1\210\1\0\1\5\4\0"+
    "\2\5\1\211\1\212\4\5\1\213\1\214\17\5\7\0"+
    "\5\5\1\0\1\5\4\0\3\5\1\215\25\5\7\0"+
    "\5\5\1\0\1\5\4\0\13\5\1\216\15\5\7\0"+
    "\5\5\1\0\1\5\4\0\21\5\1\104\7\5\7\0"+
    "\5\5\1\0\1\5\4\0\10\5\1\217\12\5\1\106"+
    "\5\5\7\0\5\5\1\0\1\5\4\0\1\220\30\5"+
    "\7\0\4\5\1\114\1\0\1\5\4\0\1\221\30\5"+
    "\7\0\5\5\1\0\1\5\4\0\1\66\4\5\1\222"+
    "\3\5\1\223\1\5\1\216\15\5\7\0\5\5\1\0"+
    "\1\5\4\0\5\5\1\224\12\5\1\225\1\5\1\226"+
    "\6\5\7\0\5\5\1\0\1\5\4\0\4\5\1\227"+
    "\24\5\7\0\5\5\1\0\1\5\4\0\12\5\1\230"+
    "\16\5\7\0\5\5\1\0\1\5\4\0\1\231\14\5"+
    "\1\232\3\5\1\233\7\5\7\0\5\5\1\0\1\234"+
    "\4\0\31\5\7\0\4\5\1\235\1\0\1\5\4\0"+
    "\1\236\1\5\1\237\1\240\1\5\1\241\2\5\1\242"+
    "\5\5\1\243\4\5\1\244\1\245\3\5\1\246\7\0"+
    "\5\5\1\0\1\5\4\0\15\5\1\247\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\16\5\1\250\12\5\7\0"+
    "\5\5\1\0\1\5\4\0\3\5\1\251\25\5\7\0"+
    "\5\5\1\0\1\5\4\0\3\5\1\106\25\5\7\0"+
    "\5\5\1\0\1\5\4\0\2\5\1\220\17\5\1\252"+
    "\6\5\7\0\5\5\1\0\1\5\4\0\4\5\1\253"+
    "\24\5\7\0\5\5\1\0\1\5\4\0\2\5\1\254"+
    "\1\106\1\5\1\255\16\5\1\256\4\5\7\0\5\5"+
    "\1\0\1\5\4\0\1\257\30\5\7\0\5\5\1\0"+
    "\1\5\4\0\17\5\1\260\11\5\7\0\5\5\1\0"+
    "\1\5\4\0\5\5\1\241\23\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\261\15\5\7\0\5\5\1\0"+
    "\1\5\4\0\1\262\30\5\7\0\5\5\1\0\1\263"+
    "\4\0\1\264\30\5\7\0\5\5\1\0\1\5\4\0"+
    "\2\5\1\265\26\5\7\0\5\5\1\0\1\5\4\0"+
    "\2\5\1\220\5\5\1\266\20\5\7\0\5\5\1\0"+
    "\1\267\4\0\13\5\1\270\15\5\7\0\5\5\1\0"+
    "\1\5\4\0\2\5\1\271\2\5\1\272\23\5\7\0"+
    "\5\5\1\0\1\273\4\0\1\274\30\5\7\0\5\5"+
    "\1\0\1\5\4\0\4\5\1\275\24\5\7\0\4\5"+
    "\1\276\1\0\1\5\4\0\3\5\1\277\25\5\7\0"+
    "\5\5\1\0\1\5\4\0\13\5\1\300\15\5\7\0"+
    "\5\5\1\0\1\5\4\0\3\5\1\176\25\5\7\0"+
    "\5\5\1\0\1\5\4\0\5\5\1\301\10\5\1\302"+
    "\12\5\7\0\5\5\1\0\1\5\4\0\1\5\1\206"+
    "\27\5\7\0\5\5\1\0\1\5\4\0\3\5\1\303"+
    "\25\5\7\0\5\5\1\0\1\5\4\0\3\5\1\304"+
    "\25\5\7\0\5\5\1\0\1\5\4\0\15\5\1\305"+
    "\13\5\7\0\5\5\1\0\1\306\4\0\15\5\1\230"+
    "\13\5\7\0\5\5\1\0\1\307\4\0\31\5\7\0"+
    "\4\5\1\310\1\0\1\5\4\0\1\5\1\311\1\5"+
    "\1\312\1\5\1\206\2\5\1\313\2\5\1\314\15\5"+
    "\7\0\5\5\1\0\1\5\4\0\5\5\1\276\23\5"+
    "\7\0\5\5\1\0\1\5\4\0\1\315\12\5\1\316"+
    "\15\5\7\0\5\5\1\0\1\5\4\0\23\5\1\317"+
    "\5\5\7\0\5\5\1\0\1\5\4\0\6\5\1\320"+
    "\22\5\7\0\5\5\1\0\1\5\4\0\2\5\1\220"+
    "\26\5\7\0\5\5\1\0\1\5\4\0\1\5\1\321"+
    "\1\322\5\5\1\323\4\5\1\324\13\5\7\0\5\5"+
    "\1\0\1\106\4\0\1\325\14\5\1\326\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\5\1\267\27\5\7\0"+
    "\5\5\1\0\1\5\4\0\4\5\1\325\24\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\5\1\327\27\5\7\0"+
    "\5\5\1\0\1\5\4\0\2\5\1\330\1\5\1\331"+
    "\24\5\7\0\5\5\1\0\1\332\4\0\13\5\1\333"+
    "\1\5\1\334\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\106\24\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\234\13\5\4\0\1\166\57\0\2\170\35\0"+
    "\1\170\13\0\2\170\1\46\34\0\1\170\11\0\5\5"+
    "\1\0\1\5\4\0\10\5\1\225\20\5\7\0\5\5"+
    "\1\0\1\5\4\0\1\335\30\5\7\0\5\5\1\0"+
    "\1\5\4\0\22\5\1\106\6\5\7\0\5\5\1\0"+
    "\1\336\4\0\11\5\1\225\17\5\7\0\5\5\1\0"+
    "\1\5\4\0\2\5\1\337\26\5\7\0\5\5\1\0"+
    "\1\5\4\0\17\5\1\106\11\5\7\0\5\5\1\0"+
    "\1\340\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\2\5\1\341\26\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\342\30\5\7\0\5\5\1\0\1\5\4\0\3\5"+
    "\1\106\4\5\1\343\20\5\7\0\5\5\1\0\1\5"+
    "\4\0\2\5\1\344\26\5\7\0\5\5\1\0\1\163"+
    "\4\0\31\5\7\0\5\5\1\0\1\5\4\0\2\5"+
    "\1\345\26\5\7\0\5\5\1\0\1\5\4\0\1\346"+
    "\30\5\7\0\5\5\1\0\1\305\4\0\1\347\30\5"+
    "\7\0\5\5\1\0\1\350\4\0\31\5\7\0\5\5"+
    "\1\0\1\5\4\0\11\5\1\351\17\5\7\0\5\5"+
    "\1\0\1\5\4\0\2\5\1\352\12\5\1\353\13\5"+
    "\7\0\5\5\1\0\1\5\4\0\1\354\30\5\7\0"+
    "\5\5\1\0\1\5\4\0\24\5\1\106\4\5\7\0"+
    "\5\5\1\0\1\5\4\0\3\5\1\355\25\5\7\0"+
    "\5\5\1\0\1\5\4\0\2\5\1\106\26\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\5\1\317\21\5\1\225"+
    "\5\5\7\0\5\5\1\0\1\5\4\0\16\5\1\106"+
    "\12\5\7\0\5\5\1\0\1\5\4\0\20\5\1\106"+
    "\10\5\7\0\5\5\1\0\1\356\4\0\31\5\7\0"+
    "\5\5\1\0\1\106\4\0\31\5\7\0\5\5\1\0"+
    "\1\5\4\0\15\5\1\114\13\5\7\0\5\5\1\0"+
    "\1\5\4\0\22\5\1\357\6\5\7\0\5\5\1\0"+
    "\1\5\4\0\2\5\1\225\26\5\7\0\5\5\1\0"+
    "\1\5\4\0\15\5\1\360\13\5\7\0\5\5\1\0"+
    "\1\5\4\0\16\5\1\361\12\5\7\0\5\5\1\0"+
    "\1\106\4\0\5\5\1\362\23\5\7\0\5\5\1\0"+
    "\1\5\4\0\5\5\1\106\23\5\7\0\5\5\1\0"+
    "\1\363\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\5\1\320\1\106\26\5\7\0\5\5\1\0\1\364"+
    "\4\0\31\5\7\0\5\5\1\0\1\5\4\0\21\5"+
    "\1\365\7\5\7\0\5\5\1\0\1\5\4\0\1\366"+
    "\30\5\7\0\5\5\1\0\1\5\4\0\3\5\1\367"+
    "\25\5\7\0\5\5\1\0\1\370\4\0\31\5\7\0"+
    "\5\5\1\0\1\5\4\0\13\5\1\247\15\5\7\0"+
    "\5\5\1\0\1\371\4\0\2\5\1\372\26\5\7\0"+
    "\5\5\1\0\1\5\4\0\21\5\1\373\7\5\7\0"+
    "\5\5\1\0\1\5\4\0\20\5\1\225\10\5\7\0"+
    "\5\5\1\0\1\5\4\0\17\5\1\114\11\5\7\0"+
    "\5\5\1\0\1\5\4\0\21\5\1\374\7\5\7\0"+
    "\5\5\1\0\1\375\4\0\31\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\376\15\5\7\0\5\5\1\0"+
    "\1\377\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\10\5\1\u0100\20\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\u0101\30\5\7\0\5\5\1\0\1\5\4\0\4\5"+
    "\1\u0102\24\5\7\0\5\5\1\0\1\u0103\4\0\31\5"+
    "\7\0\5\5\1\0\1\5\4\0\14\5\1\106\14\5"+
    "\7\0\5\5\1\0\1\5\4\0\2\5\1\u0104\26\5"+
    "\7\0\5\5\1\0\1\5\4\0\11\5\1\u0105\17\5"+
    "\7\0\5\5\1\0\1\5\4\0\3\5\1\u0106\25\5"+
    "\7\0\5\5\1\0\1\u0107\4\0\10\5\1\u0108\3\5"+
    "\1\u0109\14\5\7\0\5\5\1\0\1\106\4\0\11\5"+
    "\1\u010a\17\5\7\0\5\5\1\0\1\5\4\0\1\305"+
    "\30\5\7\0\5\5\1\0\1\5\4\0\10\5\1\356"+
    "\20\5\7\0\5\5\1\0\1\5\4\0\2\5\1\267"+
    "\16\5\1\u010b\7\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\5\1\u010c\1\5\1\u010d\4\5\1\u010e\12\5\1\u010f"+
    "\5\5\7\0\5\5\1\0\1\5\4\0\11\5\1\223"+
    "\17\5\7\0\5\5\1\0\1\5\4\0\4\5\1\u0110"+
    "\1\325\23\5\7\0\5\5\1\0\1\5\4\0\10\5"+
    "\1\41\20\5\7\0\5\5\1\0\1\5\4\0\13\5"+
    "\1\u0111\15\5\7\0\5\5\1\0\1\5\4\0\14\5"+
    "\1\u0112\14\5\7\0\5\5\1\0\1\5\4\0\12\5"+
    "\1\106\16\5\7\0\5\5\1\0\1\5\4\0\1\u0113"+
    "\30\5\7\0\5\5\1\0\1\5\4\0\15\5\1\315"+
    "\13\5\7\0\4\5\1\310\1\0\1\163\4\0\31\5"+
    "\7\0\5\5\1\0\1\5\4\0\15\5\1\u0114\13\5"+
    "\7\0\5\5\1\0\1\5\4\0\3\5\1\225\25\5"+
    "\7\0\5\5\1\0\1\5\4\0\4\5\1\225\1\106"+
    "\23\5\7\0\5\5\1\0\1\5\4\0\4\5\1\267"+
    "\24\5\7\0\5\5\1\0\1\5\4\0\15\5\1\230"+
    "\13\5\7\0\5\5\1\0\1\u0115\4\0\31\5\7\0"+
    "\5\5\1\0\1\u0116\4\0\13\5\1\106\15\5\7\0"+
    "\5\5\1\0\1\u0117\4\0\31\5\7\0\5\5\1\0"+
    "\1\5\4\0\21\5\1\114\7\5\7\0\5\5\1\0"+
    "\1\5\4\0\5\5\1\114\23\5\7\0\5\5\1\0"+
    "\1\5\4\0\21\5\1\216\7\5\7\0\5\5\1\0"+
    "\1\5\4\0\15\5\1\326\13\5\7\0\5\5\1\0"+
    "\1\5\4\0\10\5\1\106\20\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\106\15\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\273\15\5\7\0\5\5\1\0"+
    "\1\5\4\0\15\5\1\u0118\13\5\7\0\5\5\1\0"+
    "\1\5\4\0\13\5\1\234\14\5\1\u0119\7\0\5\5"+
    "\1\0\1\5\4\0\16\5\1\225\12\5\7\0\5\5"+
    "\1\0\1\5\4\0\5\5\1\222\23\5\7\0\5\5"+
    "\1\0\1\5\4\0\15\5\1\u011a\13\5\7\0\5\5"+
    "\1\0\1\5\4\0\21\5\1\224\7\5\7\0\5\5"+
    "\1\0\1\5\4\0\6\5\1\317\2\5\1\u011b\1\u011c"+
    "\16\5\7\0\5\5\1\0\1\5\4\0\11\5\1\u011d"+
    "\17\5\7\0\5\5\1\0\1\5\4\0\11\5\1\u011e"+
    "\17\5\7\0\5\5\1\0\1\5\4\0\22\5\1\301"+
    "\6\5\7\0\5\5\1\0\1\5\4\0\3\5\1\u011f"+
    "\25\5\7\0\5\5\1\0\1\5\4\0\15\5\1\u0120"+
    "\13\5\7\0\5\5\1\0\1\5\4\0\3\5\1\u0121"+
    "\25\5\7\0\5\5\1\0\1\5\4\0\15\5\1\u0122"+
    "\13\5\7\0\5\5\1\0\1\5\4\0\13\5\1\u0123"+
    "\15\5\7\0\5\5\1\0\1\5\4\0\24\5\1\u0124"+
    "\4\5\7\0\5\5\1\0\1\5\4\0\3\5\1\356"+
    "\25\5\7\0\5\5\1\0\1\5\4\0\1\164\30\5"+
    "\7\0\5\5\1\0\1\5\4\0\6\5\1\u0125\22\5"+
    "\7\0\5\5\1\0\1\5\4\0\21\5\1\u0126\7\5"+
    "\7\0\5\5\1\0\1\5\4\0\6\5\1\u0124\22\5"+
    "\7\0\5\5\1\0\1\5\4\0\4\5\1\u0127\24\5"+
    "\7\0\5\5\1\0\1\5\4\0\4\5\1\u0128\24\5"+
    "\7\0\5\5\1\0\1\5\4\0\1\u0111\30\5\7\0"+
    "\5\5\1\0\1\5\4\0\22\5\1\77\6\5\7\0"+
    "\5\5\1\0\1\5\4\0\12\5\1\u0129\16\5\7\0"+
    "\5\5\1\0\1\5\4\0\15\5\1\u012a\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\15\5\1\u012b\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\2\5\1\317\26\5\7\0"+
    "\5\5\1\0\1\5\4\0\16\5\1\206\12\5\7\0"+
    "\5\5\1\0\1\5\4\0\11\5\1\267\17\5\7\0"+
    "\5\5\1\0\1\5\4\0\4\5\1\u012c\24\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\171\30\5\7\0\5\5"+
    "\1\0\1\5\4\0\4\5\1\234\24\5\7\0\5\5"+
    "\1\0\1\5\4\0\22\5\1\225\6\5\7\0\5\5"+
    "\1\0\1\5\4\0\4\5\1\u012d\24\5\7\0\5\5"+
    "\1\0\1\5\4\0\25\5\1\216\3\5\7\0\5\5"+
    "\1\0\1\5\4\0\1\114\30\5\7\0\5\5\1\0"+
    "\1\5\4\0\1\u012e\30\5\7\0\5\5\1\0\1\5"+
    "\4\0\15\5\1\u0111\13\5\7\0\5\5\1\0\1\5"+
    "\4\0\4\5\1\77\24\5\7\0\5\5\1\0\1\5"+
    "\4\0\4\5\1\u012f\24\5\7\0\4\5\1\u0130\1\0"+
    "\1\5\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\11\5\1\114\17\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\u0131\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\u0132\24\5\7\0\5\5\1\0\1\5\4\0"+
    "\3\5\1\317\25\5\7\0\5\5\1\0\1\5\4\0"+
    "\22\5\1\u0133\6\5\7\0\5\5\1\0\1\5\4\0"+
    "\2\5\1\302\26\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\u0134\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\77\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\25\5\1\u0135\3\5\7\0\5\5\1\0\1\5\4\0"+
    "\3\5\1\267\25\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\u0136\30\5\7\0\5\5\1\0\1\5\4\0\1\u0137"+
    "\30\5\7\0\5\5\1\0\1\5\4\0\22\5\1\234"+
    "\6\5\7\0\5\5\1\0\1\5\4\0\15\5\1\u0138"+
    "\13\5\7\0\5\5\1\0\1\5\4\0\15\5\1\u0139"+
    "\13\5\7\0\5\5\1\0\1\5\4\0\3\5\1\u013a"+
    "\25\5\7\0\5\5\1\0\1\u013b\4\0\31\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\u013c\30\5\7\0\5\5"+
    "\1\0\1\5\4\0\4\5\1\225\24\5\7\0\5\5"+
    "\1\0\1\107\4\0\31\5\7\0\5\5\1\0\1\5"+
    "\4\0\4\5\1\u013d\24\5\7\0\5\5\1\0\1\5"+
    "\4\0\13\5\1\u013e\6\5\1\u013f\6\5\7\0\5\5"+
    "\1\0\1\5\4\0\25\5\1\106\3\5\7\0\5\5"+
    "\1\0\1\5\4\0\4\5\1\u0140\11\5\1\206\12\5"+
    "\7\0\5\5\1\0\1\5\4\0\4\5\1\114\1\255"+
    "\23\5\7\0\5\5\1\0\1\5\4\0\16\5\1\u0141"+
    "\12\5\7\0\5\5\1\0\1\5\4\0\21\5\1\225"+
    "\7\5\7\0\4\5\1\u0142\1\0\1\5\4\0\31\5"+
    "\7\0\5\5\1\0\1\5\4\0\17\5\1\u0143\11\5"+
    "\7\0\5\5\1\0\1\5\4\0\15\5\1\u0144\13\5"+
    "\7\0\5\5\1\0\1\5\4\0\15\5\1\u0145\13\5"+
    "\7\0\5\5\1\0\1\u0146\4\0\31\5\7\0\5\5"+
    "\1\0\1\5\4\0\26\5\1\106\2\5\7\0\5\5"+
    "\1\0\1\5\4\0\16\5\1\234\12\5\7\0\5\5"+
    "\1\0\1\u0147\4\0\31\5\7\0\4\5\1\106\1\0"+
    "\1\5\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\10\5\1\u0124\20\5\7\0\5\5\1\0\1\66\4\0"+
    "\31\5\7\0\5\5\1\0\1\5\4\0\7\5\1\225"+
    "\21\5\7\0\5\5\1\0\1\5\4\0\2\5\1\114"+
    "\26\5\7\0\5\5\1\0\1\5\4\0\22\5\1\u0148"+
    "\6\5\7\0\5\5\1\0\1\5\4\0\15\5\1\u0149"+
    "\13\5\7\0\5\5\1\0\1\5\4\0\1\u014a\30\5"+
    "\7\0\5\5\1\0\1\5\4\0\5\5\1\u014b\23\5"+
    "\7\0\5\5\1\0\1\5\4\0\5\5\1\u014c\23\5"+
    "\7\0\5\5\1\0\1\u014d\4\0\31\5\7\0\5\5"+
    "\1\0\1\5\4\0\15\5\1\377\13\5\7\0\5\5"+
    "\1\0\1\5\4\0\11\5\1\225\17\5\7\0\5\5"+
    "\1\0\1\5\4\0\15\5\1\u014e\13\5\7\0\5\5"+
    "\1\0\1\5\4\0\15\5\1\153\13\5\7\0\5\5"+
    "\1\0\1\5\4\0\3\5\1\u014f\25\5\7\0\5\5"+
    "\1\0\1\5\4\0\1\u0150\30\5\7\0\5\5\1\0"+
    "\1\5\4\0\1\320\30\5\7\0\4\5\1\u012f\1\0"+
    "\1\5\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\11\5\1\u0151\17\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\u0152\24\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\5\1\225\27\5\7\0\5\5\1\0\1\5\4\0"+
    "\3\5\1\u0153\25\5\7\0\5\5\1\0\1\5\4\0"+
    "\5\5\1\u0119\23\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\u0154\24\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\114\24\5\7\0\5\5\1\0\1\5\4\0"+
    "\11\5\1\u0155\17\5\7\0\5\5\1\0\1\5\4\0"+
    "\6\5\1\106\22\5\7\0\5\5\1\0\1\5\4\0"+
    "\5\5\1\u0156\23\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\u0125\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\23\5\1\77\5\5\7\0\5\5\1\0\1\5\4\0"+
    "\5\5\1\u0124\23\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\224\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\1\u0157\30\5\7\0\5\5\1\0\1\5\4\0\5\5"+
    "\1\301\23\5\7\0\5\5\1\0\1\5\4\0\10\5"+
    "\1\u0153\20\5\7\0\5\5\1\0\1\5\4\0\1\5"+
    "\1\u0158\27\5\7\0\5\5\1\0\1\5\4\0\25\5"+
    "\1\114\3\5\7\0\5\5\1\0\1\5\4\0\15\5"+
    "\1\u0159\13\5\7\0\5\5\1\0\1\5\4\0\12\5"+
    "\1\225\16\5\7\0\5\5\1\0\1\5\4\0\10\5"+
    "\1\u015a\20\5\7\0\5\5\1\0\1\5\4\0\11\5"+
    "\1\u015b\17\5\7\0\5\5\1\0\1\5\4\0\1\u015c"+
    "\30\5\7\0\5\5\1\0\1\5\4\0\5\5\1\u015d"+
    "\23\5\7\0\5\5\1\0\1\5\4\0\15\5\1\u015e"+
    "\13\5\7\0\5\5\1\0\1\5\4\0\3\5\1\41"+
    "\25\5\7\0\5\5\1\0\1\u015f\4\0\31\5\7\0"+
    "\5\5\1\0\1\5\4\0\5\5\1\317\23\5\7\0"+
    "\5\5\1\0\1\5\4\0\15\5\1\u0160\13\5\7\0"+
    "\5\5\1\0\1\5\4\0\1\302\30\5\7\0\5\5"+
    "\1\0\1\5\4\0\3\5\1\206\25\5\7\0\5\5"+
    "\1\0\1\5\4\0\1\u0161\30\5\7\0\5\5\1\0"+
    "\1\5\4\0\4\5\1\u0110\24\5\7\0\5\5\1\0"+
    "\1\5\4\0\21\5\1\u0111\7\5\7\0\5\5\1\0"+
    "\1\5\4\0\5\5\1\u0162\23\5\7\0\5\5\1\0"+
    "\1\320\4\0\31\5\7\0\5\5\1\0\1\5\4\0"+
    "\3\5\1\u0163\25\5\7\0\5\5\1\0\1\5\4\0"+
    "\3\5\1\u0124\25\5\7\0\5\5\1\0\1\5\4\0"+
    "\11\5\1\224\17\5\7\0\5\5\1\0\1\5\4\0"+
    "\23\5\1\225\5\5\7\0\5\5\1\0\1\5\4\0"+
    "\24\5\1\u0138\4\5\7\0\5\5\1\0\1\5\4\0"+
    "\13\5\1\234\15\5\7\0\5\5\1\0\1\5\4\0"+
    "\2\5\1\u0164\26\5\7\0\5\5\1\0\1\5\4\0"+
    "\15\5\1\u0165\13\5\7\0\5\5\1\0\1\5\4\0"+
    "\4\5\1\123\24\5\7\0\5\5\1\0\1\5\4\0"+
    "\2\5\1\u013d\26\5\7\0\5\5\1\0\1\5\4\0"+
    "\10\5\1\u0166\20\5\7\0\5\5\1\0\1\5\4\0"+
    "\3\5\1\u012f\25\5\2\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[15007];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\40\1\1\11\1\1\1\0\1\11\2\0"+
    "\13\1\2\0\100\1\1\11\2\0\13\1\1\11\342\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[358];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */

    // These will be used to store Token Start positions and length for Complex 
    // Tokens that need deifferent Lexer States, like STRING
    int tokenStart;
    int tokenLength;

    /**
     * Default constructor is needed as we will always call the yyreset
     */
    public SqlLexer() {
        super();
    }

    /**
     * Helper method to create and return a new Token from of TokenType
     */
    private Token token(TokenType type) {
        return new Token(type, yychar, yylength());
    }



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public SqlLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public SqlLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1816) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 8: 
          { return token(TokenType.STRING);
          }
        case 9: break;
        case 7: 
          { return token(TokenType.KEYWORD);
          }
        case 10: break;
        case 5: 
          { return token(TokenType.OPERATOR);
          }
        case 11: break;
        case 2: 
          { /* skip */
          }
        case 12: break;
        case 4: 
          { return token(TokenType.NUMBER);
          }
        case 13: break;
        case 3: 
          { return token(TokenType.IDENTIFIER);
          }
        case 14: break;
        case 6: 
          { return token(TokenType.COMMENT);
          }
        case 15: break;
        case 1: 
          { 
          }
        case 16: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
              {
                return null;
              }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
